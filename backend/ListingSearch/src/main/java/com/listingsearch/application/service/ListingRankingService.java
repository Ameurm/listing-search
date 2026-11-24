package com.listingsearch.application.service;

import com.listingsearch.domain.model.Listing;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ListingRankingService {
    private static final double RECENCY_DECAY_DAYS = 60.0;
    private final Clock clock;

    public ListingRankingService() {
        this(Clock.systemDefaultZone());
    }

    public ListingRankingService(Clock clock) {
        this.clock = clock;
    }

    public BigDecimal score(Listing listing, BigDecimal targetBudget) {
        double recency = recencyScore(listing.listedDate());
        double score = targetBudget == null || targetBudget.signum() <= 0
                ? recency
                : 0.60 * budgetScore(listing.price(), targetBudget) + 0.40 * recency;
        return BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP);
    }

    double budgetScore(BigDecimal price, BigDecimal targetBudget) {
        if (targetBudget == null || targetBudget.signum() <= 0) return 0.0;
        double distance = price.subtract(targetBudget).abs().doubleValue();
        return Math.max(0.0, 1.0 - distance / targetBudget.doubleValue());
    }

    double recencyScore(LocalDate listedDate) {
        long ageDays = Math.max(0, ChronoUnit.DAYS.between(listedDate, LocalDate.now(clock)));
        return Math.exp(-ageDays / RECENCY_DECAY_DAYS);
    }
}
