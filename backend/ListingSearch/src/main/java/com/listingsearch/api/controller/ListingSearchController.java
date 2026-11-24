package com.listingsearch.api.controller;

import com.listingsearch.application.dto.ListingSearchResponse;
import com.listingsearch.application.dto.SearchListingsRequest;
import com.listingsearch.application.service.ListingSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "http://localhost:5173")
public class ListingSearchController {
    private final ListingSearchService listingSearchService;

    public ListingSearchController(ListingSearchService listingSearchService) {
        this.listingSearchService = listingSearchService;
    }

    @GetMapping
    public ListingSearchResponse search(@Valid @ModelAttribute SearchListingsRequest request) {
        return listingSearchService.search(request);
    }
}
