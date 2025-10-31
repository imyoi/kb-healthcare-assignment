package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.domain.dto.HealthDataRequest;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import com.kb.healthcare.myohui.global.jwt.CustomUserDetails;
import com.kb.healthcare.myohui.service.HealthDataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthDataController {

    private final HealthDataService healthDataService;

    @Operation(summary = "건강 데이터 저장")
    @PostMapping(ApiUrl.SAVE_HEALTH_DATA)
    public BaseResponse<Void> saveHealthData(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @Valid @RequestBody HealthDataRequest request) {
        healthDataService.saveHealthData(userDetails.getMemberId(), request);
        return BaseResponse.success(null);
    }
}