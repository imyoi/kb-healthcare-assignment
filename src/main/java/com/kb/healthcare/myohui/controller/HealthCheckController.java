package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.annotation.NoPrefix;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@NoPrefix
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public BaseResponse<String> checkHealth() {
        return BaseResponse.success("서버가 정상적으로 작동 중입니다.");
    }
}