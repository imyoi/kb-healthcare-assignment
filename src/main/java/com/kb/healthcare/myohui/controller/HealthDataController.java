package com.kb.healthcare.myohui.controller;

import com.kb.healthcare.myohui.domain.dto.HealthDataRequest;
import com.kb.healthcare.myohui.domain.enums.PeriodType;
import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import com.kb.healthcare.myohui.global.jwt.CustomUserDetails;
import com.kb.healthcare.myohui.service.HealthDataService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @Operation(summary = "건강 데이터 조회")
    @GetMapping(ApiUrl.GET_HEALTH_DATA)
    public BaseResponse<?> getHealthData(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestParam(defaultValue = "DAILY") PeriodType period,
                                         @RequestParam(required = false) LocalDate startDate,
                                         @RequestParam(required = false) LocalDate endDate) {
        Long memberId = userDetails.getMemberId();
        return BaseResponse.success(healthDataService.getHealthData(memberId, period, startDate, endDate));
    }
}