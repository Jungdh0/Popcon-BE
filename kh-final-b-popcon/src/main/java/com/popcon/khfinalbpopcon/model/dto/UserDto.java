package com.popcon.khfinalbpopcon.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String token;
    private Long kakaoId;
    private String kakaoEmail;
    private String kakaoProfileImg;
    private String kakaoNickname;
}
