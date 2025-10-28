package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.global.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public BaseResponse<String> checkHealth() {
        return BaseResponse.success("서버가 정상적으로 작동 중입니다.");
    }
}