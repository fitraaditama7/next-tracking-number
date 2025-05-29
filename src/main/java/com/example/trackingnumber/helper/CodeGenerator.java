package com.example.trackingnumber.helper;

import com.example.trackingnumber.api.request.TrackingNumberRequest;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;

public class CodeGenerator {
    private static final Base32 base32 = new Base32();

    public static String generateIdempotencyKey(TrackingNumberRequest trackingNumberRequest) {
        String raw = String.join("|",
                trackingNumberRequest.getOriginCountryId(),
                trackingNumberRequest.getDestinationCountryId(),
                String.valueOf(trackingNumberRequest.getWeight()),
                String.valueOf(trackingNumberRequest.getCreatedAt().toInstant().toEpochMilli()),
                trackingNumberRequest.getCustomerId().toString(),
                trackingNumberRequest.getCustomerName(),
                trackingNumberRequest.getCustomerSlug()
        );
        return DigestUtils.sha256Hex(raw);
    }

    public static String generateTrackingCode(String input) {
        String encoded = base32.encodeToString(input.getBytes());
        return encoded.replace("=", "").substring(0, 16).toUpperCase();
    }
}
