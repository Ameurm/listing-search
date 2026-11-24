package com.listingsearch.application.service;

import com.listingsearch.application.dto.ListingSearchResponse;
import com.listingsearch.application.dto.ListingSearchResult;
import com.listingsearch.application.dto.SearchListingsRequest;
import com.listingsearch.application.repository.ListingRepository;
import com.listingsearch.domain.model.Listing;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ListingSearchService {
    private final ListingRepository repository;
    private final ListingRankingService rankingService;

    public ListingSearchService(ListingRepository repository, ListingRankingService rankingService) {
        this.repository = repository;
        this.rankingService = rankingService;
    }

    public ListingSearchResponse search(SearchListingsRequest request) {
        int page = request.getPage() == null ? 0 : request.getPage();
        int size = request.getSize() == null ? 10 : request.getSize();
        String city = normalize(request.getCity());
        String keyword = normalize(request.getKeyword());

        List<ListingSearchResult> ranked = repository.findAll().stream()
                .filter(item -> request.getMinPrice() == null || item.price().compareTo(request.getMinPrice()) >= 0)
                .filter(item -> request.getMaxPrice() == null || item.price().compareTo(request.getMaxPrice()) <= 0)
                .filter(item -> request.getMinBedrooms() == null || item.bedrooms() >= request.getMinBedrooms())
                .filter(item -> city == null || normalize(item.city()).equals(city))
                .filter(item -> keyword == null || contains(item, keyword))
                .map(item -> new ListingSearchResult(item, rankingService.score(item, request.getTargetBudget())))
                .sorted(Comparator.comparing(ListingSearchResult::relevanceScore).reversed()
                        .thenComparing((ListingSearchResult r) -> r.listing().listedDate(), Comparator.reverseOrder())
                        .thenComparing(r -> r.listing().source())
                        .thenComparing(r -> r.listing().id()))
                .toList();

        long total = ranked.size();
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
        int from = (int) Math.min((long) page * size, total);
        int to = (int) Math.min((long) from + size, total);
        return new ListingSearchResponse(ranked.subList(from, to), page, size, total, totalPages);
    }

    private boolean contains(Listing item, String keyword) {
        return normalize(item.address()).contains(keyword)
                || normalize(item.city()).contains(keyword)
                || normalize(item.description()).contains(keyword)
                || normalize(item.state()).contains(keyword)
                || normalize(item.zip()).contains(keyword);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) return null;
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
