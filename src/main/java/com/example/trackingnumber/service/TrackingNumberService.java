package com.example.trackingnumber.service;

import com.example.trackingnumber.api.response.TrackingNumberResponse;
import com.example.trackingnumber.model.entity.TrackingNumber;
import com.example.trackingnumber.api.request.TrackingNumberRequest;
import com.example.trackingnumber.repostory.TrackingNumberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.example.trackingnumber.helper.CodeGenerator.generateIdempotencyKey;
import static com.example.trackingnumber.helper.CodeGenerator.generateTrackingCode;

@Slf4j
@Service
public class TrackingNumberService {
    private final TrackingNumberRepository trackingNumberRepository;
    private final TrackingNumberRedisCache trackingNumberRedisCache;
    private final ObjectMapper objectMapper;

    public TrackingNumberService(TrackingNumberRepository trackingNumberRepository, TrackingNumberRedisCache trackingNumberRedisCache, ObjectMapper objectMapper) {
        this.trackingNumberRepository = trackingNumberRepository;
        this.trackingNumberRedisCache = trackingNumberRedisCache;
        this.objectMapper = objectMapper;
    }

    public TrackingNumber save(TrackingNumber trackingNumber) {
        return trackingNumberRepository.save(trackingNumber);
    }

    public Optional<TrackingNumber> findByIdempotencyKey(String idempotencyKey) {
        return trackingNumberRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public TrackingNumberResponse generateTrackingNumber(TrackingNumberRequest trackingNumberRequest) {
        String idempotencyKey = generateIdempotencyKey(trackingNumberRequest);

        Object cached = trackingNumberRedisCache.get(idempotencyKey);
        if (cached != null) {
            log.info("Reusing existing tracking number for idempotency key: {}", idempotencyKey);
            TrackingNumber cachedTrackingNumber = objectMapper.convertValue(cached, TrackingNumber.class);

            return buildTrackingNumberResponse(cachedTrackingNumber);
        }

        Optional<TrackingNumber> existingTrackingNumber = findByIdempotencyKey(idempotencyKey);
        if (existingTrackingNumber.isPresent()) {
            log.info("Reusing existing tracking number for idempotency key: {}", idempotencyKey);
            trackingNumberRedisCache.set(idempotencyKey, existingTrackingNumber.get());
            return buildTrackingNumberResponse(existingTrackingNumber.get());
        }

        log.info("Generating new tracking number for idempotency key: {}", idempotencyKey);

        String trackingCode = generateTrackingCode(idempotencyKey);

        TrackingNumber newTrackingNumber = TrackingNumber.builder()
                .trackingCode(trackingCode)
                .idempotencyKey(idempotencyKey)
                .build();

        try {
            TrackingNumber savedTrackingNumber = save(newTrackingNumber);
            trackingNumberRedisCache.set(idempotencyKey, savedTrackingNumber);
            return buildTrackingNumberResponse(savedTrackingNumber);
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition detected, retrieving existing tracking number", e);
            return findByIdempotencyKey(idempotencyKey)
                    .map(tn -> TrackingNumberResponse.builder()
                            .trackingNumber(tn.getTrackingCode())
                            .createdAt(tn.getCreatedAt())
                            .build())
                    .orElseThrow(() -> new RuntimeException("Tracking number conflict occurred but no record found"));
        }
    }

    private TrackingNumberResponse buildTrackingNumberResponse(TrackingNumber trackingNumber) {
        OffsetDateTime createdAt = trackingNumber.getCreatedAt().withOffsetSameInstant(ZoneOffset.ofHours(8));
        return TrackingNumberResponse.builder()
                .trackingNumber(trackingNumber.getTrackingCode())
                .createdAt(createdAt)
                .build();
    }
}
