package com.example.trackingnumber.controller;

import com.example.trackingnumber.api.request.TrackingNumberRequest;
import com.example.trackingnumber.api.response.ApiResponse;
import com.example.trackingnumber.api.response.TrackingNumberResponse;
import com.example.trackingnumber.constant.CommonConstant;
import com.example.trackingnumber.helper.CommonHelper;
import com.example.trackingnumber.service.TrackingNumberService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/next-tracking-number")
@Validated
public class TrackingNumberController {
    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberController.class);

    private final TrackingNumberService trackingNumberService;

    public TrackingNumberController(TrackingNumberService trackingNumberService) {
        this.trackingNumberService = trackingNumberService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<TrackingNumberResponse>> getNextTrackingNumber(
            @RequestParam("origin_country_id") @NotBlank String originCountryId,
            @RequestParam("destination_country_id") @NotBlank String destinationCountryId,
            @RequestParam("weight") @NotNull BigDecimal weight,
            @RequestParam("created_at") @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdAt,
            @RequestParam("customer_id") @NotNull UUID customerId,
            @RequestParam("customer_name") @NotBlank String customerName,
            @RequestParam("customer_slug") @NotBlank String customerSlug) {
        try {
            String validationError = validateInputs(originCountryId, destinationCountryId, weight, customerName, customerSlug);
            if (validationError != null) {
                return badRequest(validationError);
            }

            TrackingNumberRequest trackingNumberRequest = TrackingNumberRequest.builder()
                    .originCountryId(originCountryId)
                    .destinationCountryId(destinationCountryId)
                    .createdAt(createdAt)
                    .customerId(customerId)
                    .weight(weight)
                    .customerName(customerName)
                    .customerSlug(customerSlug)
                    .build();

            TrackingNumberResponse response = trackingNumberService.generateTrackingNumber(trackingNumberRequest);
            return ResponseEntity.ok().body(new ApiResponse<>(true, response, null));
        } catch (Exception e) {
            logger.error("failed to generate tracking number", e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, null, "Internal server error"));
        }
    }

    private String validateInputs(String originCountryId, String destinationCountryId, BigDecimal weight, String customerName, String customerSlug) {
        if (weight.compareTo(CommonConstant.MIN_WEIGHT) < 0) {
            return "Invalid weight. Minimum allowed value is " + CommonConstant.MIN_WEIGHT;
        }
        if (weight.scale() > CommonConstant.MAX_DECIMAL_PLACES) {
            return "Invalid weight. Maximum allowed decimal places is " + CommonConstant.MAX_DECIMAL_PLACES;
        }
        if (!CommonHelper.validCountryCode(originCountryId)) {
            return "Invalid origin country id";
        }
        if (!CommonHelper.validCountryCode(destinationCountryId)) {
            return "Invalid destination country id";
        }
        if (!CommonHelper.validateSlug(customerName, customerSlug)) {
            return "Invalid customer slug";
        }
        return null;
    }

    private ResponseEntity<ApiResponse<TrackingNumberResponse>> badRequest(String message) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, message));
    }
}
