package com.popcon.khfinalbpopcon.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popcon.khfinalbpopcon.config.jwt.JwtProperties;

import com.popcon.khfinalbpopcon.model.User;
import com.popcon.khfinalbpopcon.model.UserOttList;
import com.popcon.khfinalbpopcon.model.dto.UserOttListDto;
import com.popcon.khfinalbpopcon.model.kakaoLoginDto.KakaoProfile;
import com.popcon.khfinalbpopcon.model.kakaoLoginDto.OauthToken;
import com.popcon.khfinalbpopcon.repository.UserOttListRepository;
import com.popcon.khfinalbpopcon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.popcon.khfinalbpopcon.config.SecurityConfig.FRONT_URL;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserOttListRepository userOttListRepository;

    //환경 변수 가져오기
    @Value("${kakao.clientId}")
    String client_id;

    @Value("${kakao.secret}")
    String client_secret;

    public OauthToken getAccessToken(String code) {

        // POST 방식으로 key=value 데이터 요청
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("redirect_uri", FRONT_URL + "/auth");
        params.add("code", code);
        params.add("client_secret", client_secret);

        // HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청 (POST 방식) 후, response 변수의 응답을 받음
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 응답을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        OauthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(accessTokenResponse.getBody(), OauthToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oauthToken;
    }

    public KakaoProfile findProfile(String token) {
        // POST 방식으로 key=value 데이터 요청
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader 와 HttpBody 정보를 하나의 오브젝트에 담음
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers);

        // Http 요청 (POST 방식) 후, response 변수의 응답을 받음
        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        // JSON 응답을 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }

    public User getUser(HttpServletRequest request) {
        Long userCode = (Long) request.getAttribute("userCode");

        User user = userRepository.findByUserCode(userCode);

        return user;
    }

    public String SaveUserAndGetToken(String token) {
        KakaoProfile profile = findProfile(token);

        User user = userRepository.findByKakaoEmail(profile.getKakao_account().getEmail());
        if(user == null) {
            user = User.builder()
                    .kakaoId(profile.getId())
                    .kakaoProfileImg(profile.getKakao_account().getProfile().getProfile_image_url())
                    .kakaoNickname(profile.getKakao_account().getProfile().getNickname())
                    .kakaoEmail(profile.getKakao_account().getEmail())
                    .userRole("ROLE_USER").build();

            userRepository.save(user);
        }

        return createToken(user);
    }

    public String createToken(User user) {
        // Jwt 생성 후 헤더에 추가해서 보내줌
        String jwtToken = JWT.create()
                .withSubject(user.getKakaoEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ JwtProperties.EXPIRATION_TIME))
                .withClaim("id", user.getUserCode())
                .withClaim("nickname", user.getKakaoNickname())
                .sign(Algorithm.HMAC512(JwtRequestFilter.SECRET));

        return jwtToken;
    }

    public void updateOtt(HttpServletRequest request, UserOttListDto dto) {

        // 받은 dto 객체의 필드를 Map에 넣어 반환하는 메소드 실행
        Map<String, Boolean> ottMap = dto.toMap();

        // 빌드용 객체
        UserOttList userOttList = null;

        Long userCode = getUser(request).getUserCode();

        for(String key : ottMap.keySet()) {
            // 쿼리 실행후 담아줄 객체
            UserOttList ottList = userOttListRepository.findByUserCodeAndOttName(userCode, key);

            // 확장 for 문으로 map 에 담은 key, value 값 하나씩 검증
            // 분기로 db 데이터 삽입 또는 삭제 실행
            if(ottMap.get(key) == true) {
                if(ottList == null) {
                    userOttList = new UserOttList().builder()
                            .userCode(userCode)
                            .ottName(key).build();

                    userOttListRepository.save(userOttList);
                }
            } else if (ottMap.get(key) == false){
                if(ottList != null) {
                    userOttListRepository.deleteById(ottList.getUserOttIndex());
                }
            }
        }
    }

    public UserOttListDto getUserOtt(HttpServletRequest request) {

        Long userCode = getUser(request).getUserCode();

        // 리스트에 해당 객체 담기
        List<UserOttList> list = userOttListRepository.findByUserCode(userCode);
        // 리턴할 dto 생성
        // 기본 생성자를 사용하면 필드값이 기본 false 로 생성됨
        UserOttListDto dto = new UserOttListDto();
        // 반복문으로 dto 객체에 담기
        Iterator<UserOttList> iter = list.iterator();
        while(iter.hasNext()){
            UserOttList ottList = iter.next();
            switch (ottList.getOttName()) {
                case "netflix": dto.setNetflix(true);
                break;

                case "wavve": dto.setWavve(true);
                break;

                case "tving": dto.setTving(true);
                break;
            }
        }

        return dto;
    }

}
