package com.kb.healthcare.myohui.global.exception;

import com.kb.healthcare.myohui.global.BaseResponse;
import com.kb.healthcare.myohui.global.enums.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "com.kb.healthcare.myohui")
public class GlobalExceptionHandler {

    /**
     * 유효성 검증 에러 (HTTP 200)
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        return BaseResponse.error(
            new BaseResponse.Error(
                ErrorCode.COMMON_INVALID_REQUEST.getCode(),
                String.join(", ", messages)
            )
        );
    }

    /**
     * 비즈니스 로직 에러 (HTTP 200)
     * */
    @ExceptionHandler(CustomException.class)
    public BaseResponse<?> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return BaseResponse.error(
            new BaseResponse.Error(code.getCode(), code.getMessage())
        );
    }

    /**
     * 이 외 에러 (HTTP 500)
     * */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
        return ResponseEntity
            .internalServerError()
            .body(BaseResponse.error(
                new BaseResponse.Error(
                    ErrorCode.COMMON_INTERNAL_SERVER.getCode(),
                    ErrorCode.COMMON_INTERNAL_SERVER.getMessage()
                )
            ));
    }
}