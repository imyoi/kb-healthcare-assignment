package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.domain.dto.MemberLoginRequest;
import com.kb.healthcare.myohui.domain.dto.MemberLoginResponse;
import com.kb.healthcare.myohui.domain.dto.MemberResponse;
import com.kb.healthcare.myohui.domain.dto.MemberSignupRequest;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import com.kb.healthcare.myohui.global.jwt.CustomUserDetails;
import com.kb.healthcare.myohui.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping(ApiUrl.SIGNUP)
    public BaseResponse<MemberResponse> signup(@Valid @RequestBody MemberSignupRequest request) {
        MemberResponse response = memberService.signup(request);
        return BaseResponse.success(response);
    }

    @Operation(summary = "로그인")
    @PostMapping(ApiUrl.LOGIN)
    public BaseResponse<MemberLoginResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        MemberLoginResponse response = memberService.login(request);
        return BaseResponse.success(response);
    }

    @Operation(summary = "회원 조회")
    @GetMapping(ApiUrl.GET_MEMBER)
    public BaseResponse<MemberResponse> getMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberResponse response = memberService.getMemberById(userDetails.getMemberId());
        return BaseResponse.success(response);
    }
}