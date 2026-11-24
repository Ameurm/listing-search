package com.listingsearch.application.dto;

import java.util.List;

public record ListingSearchResponse(
        List<ListingSearchResult> results,
        int page,
        int size,
        long totalElements,
        int totalPages) {
}
