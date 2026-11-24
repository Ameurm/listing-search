package com.listingsearch.infrastructure.persistence;

import com.listingsearch.application.repository.ListingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class JsonListingRepositoryTests {
    @Autowired
    private ListingRepository repository;

    @Test
    void loadsTwelveSampleListings() {
        assertEquals(12, repository.findAll().size());
        assertEquals(11, repository.findAll().stream().filter(x -> "ACTIVE".equals(x.status())).count());
    }
}
