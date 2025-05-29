package com.example.trackingnumber.controller;

import com.example.trackingnumber.api.request.TrackingNumberRequest;
import com.example.trackingnumber.api.response.ApiResponse;
import com.example.trackingnumber.api.response.TrackingNumberResponse;
import com.example.trackingnumber.constant.CommonConstant;
import com.example.trackingnumber.service.TrackingNumberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrackingNumberControllerTest {
    private TrackingNumberService trackingNumberService;
    private TrackingNumberController trackingNumberController;

    @BeforeEach
    void setUp() {
        trackingNumberService = mock(TrackingNumberService.class);
        trackingNumberController = new TrackingNumberController(trackingNumberService);
    }

    @Test
    void getNextTrackingNumber_shouldReturnSuccessResponse() {
        String originCountryId = "MY";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("1.0");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        TrackingNumberResponse mockResponse = TrackingNumberResponse.builder()
                .trackingNumber("ABCDABCDABCDABCD")
                .createdAt(createdAt)
                .build();

        when(trackingNumberService.generateTrackingNumber(any(TrackingNumberRequest.class))).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("ABCDABCDABCDABCD", response.getBody().getData().getTrackingNumber());
    }

    @Test
    void getPreviousTrackingNumber_shouldReturnEmptyWeightError() {
        String originCountryId = "MY";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("0.0");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid weight. Minimum allowed value is " + CommonConstant.MIN_WEIGHT, response.getBody().getError());
    }

    @Test
    void getPreviousTrackingNumber_shouldReturnMaximumDecimalPlacesError() {
        String originCountryId = "MY";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("0.11111111111");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid weight. Maximum allowed decimal places is " + CommonConstant.MAX_DECIMAL_PLACES, response.getBody().getError());
    }

    @Test
    void getPreviousTrackingNumber_shouldReturnInvalidOriginCountryIDError() {
        String originCountryId = "MYS";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("0.111");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid origin country id", response.getBody().getError());
    }

    @Test
    void getPreviousTrackingNumber_shouldReturnInvalidDestinationCountryIDError() {
        String originCountryId = "MY";
        String destinationCountryId = "MYS";
        BigDecimal weight = new BigDecimal("0.111");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid destination country id", response.getBody().getError());
    }


    @Test
    void getPreviousTrackingNumber_shouldReturnCustomerSlugError() {
        String originCountryId = "MY";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("0.111");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-does";

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid customer slug", response.getBody().getError());
    }

    @Test
    void getNextTrackingNumber_shouldReturnInternalServerError() {
        String originCountryId = "MY";
        String destinationCountryId = "MY";
        BigDecimal weight = new BigDecimal("1.0");
        OffsetDateTime createdAt = OffsetDateTime.now();
        UUID customerId = UUID.randomUUID();
        String customerName = "John Doe";
        String customerSlug = "john-doe";

        TrackingNumberResponse mockResponse = TrackingNumberResponse.builder()
                .trackingNumber("ABCDABCDABCDABCD")
                .createdAt(createdAt)
                .build();

        when(trackingNumberService.generateTrackingNumber(any(TrackingNumberRequest.class))).thenThrow(new RuntimeException("internal server error"));

        ResponseEntity<ApiResponse<TrackingNumberResponse>> response = trackingNumberController.getNextTrackingNumber(
                originCountryId,
                destinationCountryId,
                weight,
                createdAt,
                customerId,
                customerName,
                customerSlug
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Internal server error", response.getBody().getError());
    }
}
