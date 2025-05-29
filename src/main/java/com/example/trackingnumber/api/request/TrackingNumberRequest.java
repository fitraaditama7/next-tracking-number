package com.example.trackingnumber.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class TrackingNumberRequest {
    @NotBlank
    @Size(min = 2, max=2)
    private String originCountryId;

    @NotBlank
    @Size
    private String destinationCountryId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal weight;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private UUID customerId;

    @NotBlank
    private String customerName;

    @NotBlank
    private String customerSlug;
}
