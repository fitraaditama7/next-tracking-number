package com.example.trackingnumber.service;

import com.example.trackingnumber.api.request.TrackingNumberRequest;
import com.example.trackingnumber.api.response.TrackingNumberResponse;
import com.example.trackingnumber.model.entity.TrackingNumber;
import com.example.trackingnumber.repostory.TrackingNumberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.trackingnumber.helper.CodeGenerator.generateIdempotencyKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackingNumberServiceTest {
    @Mock
    TrackingNumberRepository trackingNumberRepository;

    @Mock
    TrackingNumberRedisCache trackingNumberRedisCache;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    TrackingNumberService trackingNumberService;

    TrackingNumberRequest request;
    String idempotencyKey;
    TrackingNumber trackingNumber;

    @BeforeEach
    void SetUp() {
        request = TrackingNumberRequest.builder()
                .originCountryId("ID")
                .destinationCountryId("US")
                .weight(new BigDecimal("0.001"))
                .createdAt(OffsetDateTime.now())
                .customerId(UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"))
                .customerName("John Doe")
                .customerSlug("john-doe")
                .build();
        idempotencyKey = generateIdempotencyKey(request);
        trackingNumber = TrackingNumber.builder()
                .idempotencyKey(idempotencyKey)
                .trackingCode("ABC123")
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void generateTrackingNumber_shouldReturnFromCache() {
        when(trackingNumberRedisCache.get(idempotencyKey)).thenReturn(trackingNumber);
        when(objectMapper.convertValue(trackingNumber, TrackingNumber.class)).thenReturn(trackingNumber);

        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        assertEquals("ABC123", response.getTrackingNumber());
        verify(trackingNumberRedisCache).get(idempotencyKey);
        verifyNoInteractions(trackingNumberRepository);
    }

    @Test
    void generateTrackingNumber_shouldReturnFromDBWhenCacheIsEmpty() {
        when(trackingNumberRedisCache.get(idempotencyKey)).thenReturn(null);
        when(trackingNumberRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(trackingNumber));

        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        assertEquals("ABC123", response.getTrackingNumber());
        verify(trackingNumberRedisCache).set(idempotencyKey, trackingNumber);
    }

    @Test
    void generateTrackingNumber_shouldGenerateNewTrackingNumber() {
        when(trackingNumberRedisCache.get(idempotencyKey)).thenReturn(null);
        when(trackingNumberRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(trackingNumberRepository.save(any())).thenReturn(trackingNumber);

        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        assertEquals("ABC123", response.getTrackingNumber());
        verify(trackingNumberRepository).save(any());
        verify(trackingNumberRedisCache).set(idempotencyKey, trackingNumber);
    }

    @Test
    void generateTrackingNumber_shouldHandleRaceConditions() {
        when(trackingNumberRedisCache.get(idempotencyKey)).thenReturn(null);
        when(trackingNumberRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty())
                .thenReturn(Optional.of(trackingNumber));
        when(trackingNumberRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(request);

        assertEquals("ABC123", response.getTrackingNumber());
        verify(trackingNumberRepository, times(2)).findByIdempotencyKey(idempotencyKey);
    }

    @Test
    void generateTrackingNumber_shouldThrowWhenRaceConditionAndNoRecord() {
        when(trackingNumberRedisCache.get(idempotencyKey)).thenReturn(null);
        when(trackingNumberRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(trackingNumberRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trackingNumberService.generateTrackingNumber(request));

        assertEquals("Tracking number conflict occurred but no record found", ex.getMessage());
    }
}
