package com.listingsearch.infrastructure.persistence;

import com.listingsearch.application.repository.ListingRepository;
import com.listingsearch.domain.model.Listing;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Repository
public class JsonListingRepository implements ListingRepository {
    private final List<Listing> listings;

    public JsonListingRepository(JsonMapper jsonMapper) {
        try (InputStream input = new ClassPathResource("sample_listings.json").getInputStream()) {
            this.listings = List.copyOf(Arrays.asList(jsonMapper.readValue(input, Listing[].class)));
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load sample_listings.json", exception);
        }
    }

    @Override
    public List<Listing> findAll() {
        return listings;
    }
}
