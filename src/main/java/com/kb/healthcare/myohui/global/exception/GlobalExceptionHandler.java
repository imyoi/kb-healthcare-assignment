package com.kb.healthcare.myohui.global.exception;

import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.kb.healthcare.myohui")
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<?>> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
            .status(code.getStatus())
            .body(BaseResponse.error(code.getCode(), code.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        return BaseResponse.error(ErrorCode.COMMON_INTERNAL_SERVER.getCode(), e.getMessage());
    }
}