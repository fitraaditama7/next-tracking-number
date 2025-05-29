package com.example.trackingnumber.api.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class TrackingNumberResponse {
    private String trackingNumber;
    private OffsetDateTime createdAt;
}
