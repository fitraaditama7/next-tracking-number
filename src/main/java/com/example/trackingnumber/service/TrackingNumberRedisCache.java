package com.example.trackingnumber.service;

import com.example.trackingnumber.model.entity.TrackingNumber;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TrackingNumberRedisCache {
    private static final String PREFIX = "tracking_number:";
    private final RedisTemplate<String, Object> redisTemplate;

    public TrackingNumberRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Object get(String idempotencyKey) {
        return redisTemplate.opsForValue().get(PREFIX + idempotencyKey);
    }

    public void set(String idempotencyKey, TrackingNumber trackingNumber) {
        redisTemplate.opsForValue().set(PREFIX + idempotencyKey, trackingNumber, Duration.ofMinutes(5));
    }
}
