package com.listingsearch.application.service;

import com.listingsearch.domain.model.Listing;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListingRankingServiceTests {
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-23T12:00:00Z"), ZoneOffset.UTC);
    private final ListingRankingService service = new ListingRankingService(clock);

    @Test
    void exactBudgetAndTodayGetsMaximumScore() {
        Listing listing = listing("500000", LocalDate.now(clock));
        assertEquals(new BigDecimal("1.0000"), service.score(listing, new BigDecimal("500000")));
    }

    @Test
    void noBudgetUsesRecencyOnly() {
        Listing listing = listing("450000", LocalDate.now(clock));
        assertEquals(new BigDecimal("1.0000"), service.score(listing, null));
    }

    @Test
    void olderListingHasLowerScore() {
        Listing recent = listing("500000", LocalDate.now(clock));
        Listing older = listing("500000", LocalDate.now(clock).minusDays(60));
        org.junit.jupiter.api.Assertions.assertTrue(service.score(recent, new BigDecimal("500000"))
                .compareTo(service.score(older, new BigDecimal("500000"))) > 0);
    }

    private Listing listing(String price, LocalDate date) {
        return new Listing("T1", "TEST", "1 Test St", "Fairfax", "VA", "22030", new BigDecimal(price),
                3, new BigDecimal("2.0"), 1500, null, null, date, "ACTIVE", "Test listing");
    }
}
