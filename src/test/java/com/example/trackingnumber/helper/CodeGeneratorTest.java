package com.example.trackingnumber.helper;

import com.example.trackingnumber.api.request.TrackingNumberRequest;
import org.aspectj.apache.bcel.classfile.Code;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class CodeGeneratorTest {

    @Test
    void generateIdempotencyKey_shouldReturnConsistentSHA256Hash() {
        TrackingNumberRequest trackingNumberRequest = TrackingNumberRequest.builder()
                .originCountryId("MY")
                .destinationCountryId("MY")
                .weight(new BigDecimal("0.01"))
                .customerName("test")
                .customerSlug("test")
                .customerId(UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"))
                .createdAt(OffsetDateTime.of(2025, 5, 29, 10, 0, 0, 0, ZoneOffset.UTC))
                .build();

        String key1 = CodeGenerator.generateIdempotencyKey(trackingNumberRequest);
        String key2 = CodeGenerator.generateIdempotencyKey(trackingNumberRequest);

        assert !key1.isEmpty();
        assert key1.equals(key2);
    }

    @Test
    void generateTrackingCode_shouldReturnConsistentTrackingCode() {
        TrackingNumberRequest trackingNumberRequest = TrackingNumberRequest.builder()
                .originCountryId("MY")
                .destinationCountryId("MY")
                .weight(new BigDecimal("0.01"))
                .customerName("test")
                .customerSlug("test")
                .customerId(UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"))
                .createdAt(OffsetDateTime.of(2025, 5, 29, 10, 0, 0, 0, ZoneOffset.UTC))
                .build();

        String idempotencyKey = CodeGenerator.generateIdempotencyKey(trackingNumberRequest);
        String trackingCode1 = CodeGenerator.generateTrackingCode(idempotencyKey);
        String trackingCode2 = CodeGenerator.generateTrackingCode(idempotencyKey);

        assert !trackingCode1.isEmpty();
        assert trackingCode1.equals(trackingCode2);
    }
}
