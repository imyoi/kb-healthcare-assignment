package com.kb.healthcare.myohui.global.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    @Qualifier("stringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        String token = redisTemplate.opsForValue().get(key);
        if (token == null || token.isBlank()) {
            return null;
        }
        return token;
    }

    public void setRefreshToken(Long memberId, String refreshToken, long expireSeconds) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(expireSeconds));
    }

    public void deleteRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        Boolean deleted = redisTemplate.delete(key);
    }
}