package com.kb.healthcare.myohui.global.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // --- 공통 (COMMON) ---
    COMMON_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_INVALID_REQUEST", "잘못된 요청입니다."),
    COMMON_MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_MISSING_PARAMETER", "필수 요청 파라미터가 누락되었습니다."),
    COMMON_DUPLICATE_REQUEST(HttpStatus.CONFLICT, "COMMON_DUPLICATE_REQUEST", "중복된 요청입니다."),
    COMMON_INTERNAL_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_INTERNAL_SERVER", "서버 내부 오류가 발생했습니다."),
    COMMON_PROVIDER_ERROR(HttpStatus.BAD_GATEWAY, "COMMON_PROVIDER_ERROR", "외부 서비스에서 오류가 발생했습니다."),

    // --- 인증 (AUTH) ---
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_UNAUTHORIZED", "인증이 필요합니다."),
    AUTH_INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH_FORBIDDEN", "접근 권한이 없습니다."),

    // --- 회원 (MEMBER) ---
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "MEMBER_ALREADY_EXISTS", "이미 등록된 회원입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_EXISTS", "이미 등록된 이메일입니다."),
    MEMBER_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER_PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),
    MEMBER_INVALID_PASSWORD_RULE(HttpStatus.BAD_REQUEST, "MEMBER_INVALID_PASSWORD_RULE", "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."),

    // --- 건강데이터 (HEALTH) ---
    HEALTH_INVALID_DATE(HttpStatus.BAD_REQUEST, "HEALTH_INVALID_DATE", "유효하지 않은 날짜 형식입니다."),
    HEALTH_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "HEALTH_RECORD_NOT_FOUND", "해당 건강데이터를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}