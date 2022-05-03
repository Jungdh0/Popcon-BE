package com.popcon.khfinalbpopcon.controller;

import com.popcon.khfinalbpopcon.config.jwt.JwtProperties;
import com.popcon.khfinalbpopcon.model.User;
import com.popcon.khfinalbpopcon.model.dto.UserOttListDto;
import com.popcon.khfinalbpopcon.model.kakaoLoginDto.OauthToken;
import com.popcon.khfinalbpopcon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 프론트에서 인가코드 돌려 받는 주소
    // 인가 코드로 엑세스 토큰 발급 -> 사용자 정보 조회 -> DB 저장 -> jwt 토큰 발급 -> 프론트에 토큰 전달
    @GetMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code) {

        // 넘어온 인가 코드를 통해 access_token 발급
        OauthToken oauthToken = userService.getAccessToken(code);

        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장
        String jwtToken = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return ResponseEntity.ok().headers(headers).body("success");
    }

    // jwt 토큰으로 유저정보 요청하기
    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) {

        User user = userService.getUser(request);

        return ResponseEntity.ok().body(user);
    }

    // 사용자의 ott 구독 목록 수정 컨트롤러
    @PutMapping("/me/ott")
    public ResponseEntity updateOtt(HttpServletRequest request, @RequestBody UserOttListDto userOttListDto){
        System.out.println("UserOttListDto : "+ userOttListDto);//잘들어옴!

        // dto 객체 업데이트
        userService.updateOtt(request, userOttListDto);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    // 사용자의 ott 구독 목록 출력 컨트롤러
    @GetMapping("/me/ott")
    public UserOttListDto getUserOtt(HttpServletRequest request) {

        return userService.getUserOtt(request);
    }

}
