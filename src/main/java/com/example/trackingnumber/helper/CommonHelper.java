package com.example.trackingnumber.helper;

import java.util.Arrays;
import java.util.Locale;

public class CommonHelper {
    public static boolean validateSlug(String customerName, String customerSlug) {
        String expectedCustomerSlug = toKebabCase(customerName);
        return customerSlug.equals(expectedCustomerSlug);
    }

    public static String toKebabCase(String input) {
        return input == null ? "" : input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public static boolean validCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            return false;
        }
        String[] countryCodes = Locale.getISOCountries();
        return Arrays.asList(countryCodes).contains(countryCode);
    }
}
