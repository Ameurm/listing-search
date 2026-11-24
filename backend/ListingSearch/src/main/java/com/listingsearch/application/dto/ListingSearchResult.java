package com.listingsearch.application.dto;

import com.listingsearch.domain.model.Listing;

import java.math.BigDecimal;

public record ListingSearchResult(Listing listing, BigDecimal relevanceScore) {
}
