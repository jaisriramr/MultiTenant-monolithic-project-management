package com.projectmanagement.multitenantprojectmanagement.config;

import java.time.Duration;
import java.util.Collections;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMITER_SCRIPT = 
            "local current " +
            "current = redis.call('incr', KEYS[1]) " +
            "if tonumber(current) == 1 then " + 
            "  redis.call('expire', KEYS[1], ARGV[1]) " +
            "end " +
            "return current";
    
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String key, int maxRequests, int timeWindow) {
        Long currentCount = redisTemplate.execute(new DefaultRedisScript<>(RATE_LIMITER_SCRIPT, Long.class), Collections.singletonList(key), timeWindow);
        return currentCount != null && currentCount <= maxRequests;
    }

    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
