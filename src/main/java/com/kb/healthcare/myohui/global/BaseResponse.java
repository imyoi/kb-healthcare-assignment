package com.kb.healthcare.myohui.global;

import com.kb.healthcare.myohui.global.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {

    private boolean success;
    private final T response;
    private Error error;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, data, null);
    }

    public static BaseResponse<Void> error(Error error) {
        return new BaseResponse<>(false, null, error);
    }

    public record Error(String code, String message) {
        public static Error of(ErrorCode errorCode) {
            return new Error(errorCode.getCode(), errorCode.getMessage());
        }
    }
}