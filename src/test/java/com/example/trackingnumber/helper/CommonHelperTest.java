package com.example.trackingnumber.helper;

import org.junit.jupiter.api.Test;

public class CommonHelperTest {
    @Test
    void validateSlug_shouldReturnTrue() {
        String customerName = "John Doe";
        String expectedSlug = "john-doe";

        boolean result = CommonHelper.validateSlug(customerName, expectedSlug);
        assert result;
    }

    @Test
    void validateSlug_shouldReturnFalse() {
        String customerName = "John Doe";
        String expectedSlug = "john-taslim";

        boolean result = CommonHelper.validateSlug(customerName, expectedSlug);
        assert !result;
    }

    @Test
    void toKebabCase_shouldReturnEmptyString_WhenInputIsEmpty() {
        String result = CommonHelper.toKebabCase(null);
        assert result.isEmpty();
    }

    @Test
    void validCountryCode_shouldReturnTrue() {
        boolean result = CommonHelper.validCountryCode("US");
        assert result;
    }

    @Test
    void validCountryCode_shouldReturnFalse() {
        boolean result = CommonHelper.validCountryCode("USA");
        assert !result;
    }

    @Test
    void validCountryCode_shouldReturnFalse_WhenInputIsEmpty() {
        boolean result = CommonHelper.validCountryCode("");
        assert !result;
    }

    @Test
    void validCountryCode_shouldReturnFalse_WhenInputIsNull() {
        boolean result = CommonHelper.validCountryCode(null);
        assert !result;
    }
}
