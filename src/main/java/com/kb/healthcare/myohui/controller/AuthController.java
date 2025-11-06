package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.domain.dto.MemberLoginRequest;
import com.kb.healthcare.myohui.domain.dto.MemberLoginResponse;
import com.kb.healthcare.myohui.domain.dto.TokenRefreshRequest;
import com.kb.healthcare.myohui.domain.dto.TokenRefreshResponse;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import com.kb.healthcare.myohui.global.jwt.CustomUserDetails;
import com.kb.healthcare.myohui.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping(ApiUrl.LOGIN)
    public BaseResponse<MemberLoginResponse> login(@Valid @RequestBody MemberLoginRequest request) {
        MemberLoginResponse response = authService.login(request);
        return BaseResponse.success(response);
    }

    @Operation(summary = "로그아웃")
    @PostMapping(ApiUrl.LOGOUT)
    public BaseResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getMemberId());
        return BaseResponse.success(null);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping(ApiUrl.REFRESH)
    public BaseResponse<TokenRefreshResponse> refresh(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse response = authService.refresh(userDetails.getMemberId(), request.getRefreshToken());
        return BaseResponse.success(response);
    }
}