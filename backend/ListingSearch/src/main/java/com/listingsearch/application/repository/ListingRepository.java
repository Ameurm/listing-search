package com.listingsearch.application.repository;

import com.listingsearch.domain.model.Listing;
import java.util.List;

public interface ListingRepository {
    List<Listing> findAll();
}
