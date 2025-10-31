package com.kb.healthcare.myohui.global.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiUrl {

    public static final String API_PREFIX = "/api/v1";

    // --- 회원 (MEMBER) ---
    public static final String SIGNUP = "/members/signup";
    public static final String LOGIN = "/members/login";
    public static final String GET_MEMBER = "/members";

    // --- 건강 (HEALTH) ---
    public static final String SAVE_HEALTH_DATA = "/health/data";
}