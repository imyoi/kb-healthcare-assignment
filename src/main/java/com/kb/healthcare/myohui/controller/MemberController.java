package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.domain.dto.MemberResponse;
import com.kb.healthcare.myohui.domain.dto.MemberSignupRequest;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import com.kb.healthcare.myohui.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        return BaseResponse.success("회원가입이 완료되었습니다.", response);
    }
}