package com.example.trackingnumber.service;

import com.example.trackingnumber.model.entity.TrackingNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrackingNumberRedisCacheTest {
    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;
    private TrackingNumberRedisCache cache;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        cache = new TrackingNumberRedisCache(redisTemplate);
    }

    @Test
    void get_shouldReturnCachedTrackingNumber() {
        String key = "123";
        String redisKey = "tracking_number:" + key;

        TrackingNumber expected = TrackingNumber.builder()
                .trackingCode("12345")
                .build();

        when(valueOperations.get(redisKey)).thenReturn(expected);

        Object result = cache.get(key);

        assertEquals(expected, result);
        verify(valueOperations).get(redisKey);
    }

    @Test
    void set_shouldStoreTrackingNumberInRedis() {
        String key = "test-key";
        String redisKey = "tracking_number:" + key;
        TrackingNumber trackingNumber = new TrackingNumber();

        cache.set(key, trackingNumber);

        verify(valueOperations).set(redisKey, trackingNumber, Duration.ofMinutes(5));
    }
}
