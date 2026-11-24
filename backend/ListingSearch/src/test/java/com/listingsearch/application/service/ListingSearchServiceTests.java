package com.listingsearch.application.service;

import com.listingsearch.application.dto.SearchListingsRequest;
import com.listingsearch.application.repository.ListingRepository;
import com.listingsearch.domain.model.Listing;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ListingSearchServiceTests {
    private final ListingRepository repository = () -> List.of(
            listing("1", "Springfield", "450000", 3, "pet friendly home"),
            listing("2", "Springfield", "525000", 4, "renovated kitchen"),
            listing("3", "Fairfax", "399000", 2, "condo near shops"));
    private final ListingSearchService service = new ListingSearchService(repository,
            new ListingRankingService(Clock.systemUTC()));

    @Test
    void filtersByCityAndMinimumBedrooms() {
        SearchListingsRequest request = new SearchListingsRequest();
        request.setCity(" springfield ");
        request.setMinBedrooms(4);
        var response = service.search(request);
        assertEquals(1, response.totalElements());
        assertEquals("2", response.results().getFirst().listing().id());
    }

    @Test
    void filtersByPriceRangeAndKeyword() {
        SearchListingsRequest request = new SearchListingsRequest();
        request.setMinPrice(new BigDecimal("400000"));
        request.setMaxPrice(new BigDecimal("500000"));
        request.setKeyword("PET");
        var response = service.search(request);
        assertEquals(1, response.totalElements());
        assertEquals("1", response.results().getFirst().listing().id());
    }

    @Test
    void paginatesFilteredResults() {
        SearchListingsRequest request = new SearchListingsRequest();
        request.setCity("Springfield");
        request.setSize(1);
        request.setPage(1);
        var response = service.search(request);
        assertEquals(2, response.totalElements());
        assertEquals(2, response.totalPages());
        assertEquals(1, response.results().size());
    }

    private Listing listing(String id, String city, String price, int bedrooms, String description) {
        return new Listing(id, "TEST", "1 Main St", city, "VA", "22030", new BigDecimal(price), bedrooms,
                new BigDecimal("2.0"), 1500, null, null, LocalDate.now(), "ACTIVE", description);
    }
}
