package com.listingsearch.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Listing(
        String id,
        String source,
        String address,
        String city,
        String state,
        String zip,
        BigDecimal price,
        int bedrooms,
        BigDecimal bathrooms,
        int sqft,
        Double latitude,
        Double longitude,
        LocalDate listedDate,
        String status,
        String description) {
}
