package com.kb.healthcare.myohui.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {

    private final String code;
    private final String message;
    private final T data;

    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>("SUCCESS", message, data);
    }

    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>("SUCCESS", message, null);
    }

    public static BaseResponse<?> error(String code, String message) {
        return new BaseResponse<>(code, message, null);
    }
}