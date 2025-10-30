package com.kb.healthcare.myohui.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponse {

    private String accessToken;
    private String refreshToken;
}