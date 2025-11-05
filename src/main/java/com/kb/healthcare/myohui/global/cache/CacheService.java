package com.kb.healthcare.myohui.global.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheService {

    @Qualifier("jsonRedisTemplate")
    private final RedisTemplate<String, Object> jsonRedisTemplate;

    public <T> T getCache(String key, Class<T> type) {
        Object value = jsonRedisTemplate.opsForValue().get(key);
        if (value != null) {
            log.info("[CACHE HIT] key={}", key);
            return type.cast(value);
        }
        log.info("[CACHE MISS] key={}", key);
        return null;
    }

    public void setCache(String key, Object value, long hours) {
        jsonRedisTemplate.opsForValue().set(key, value, Duration.ofHours(hours));
    }

    public void deleteCache(String key) {
        jsonRedisTemplate.delete(key);
    }
}