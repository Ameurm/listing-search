package com.listingsearch.application.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchListingsRequestValidationTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsMinimumPriceGreaterThanMaximumPrice() {
        SearchListingsRequest request = new SearchListingsRequest();
        request.setMinPrice(new BigDecimal("600000"));
        request.setMaxPrice(new BigDecimal("300000"));
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void acceptsValidPriceRange() {
        SearchListingsRequest request = new SearchListingsRequest();
        request.setMinPrice(new BigDecimal("300000"));
        request.setMaxPrice(new BigDecimal("600000"));
        assertTrue(validator.validate(request).isEmpty());
    }
}
