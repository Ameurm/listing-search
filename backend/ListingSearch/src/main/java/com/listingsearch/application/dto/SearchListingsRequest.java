package com.listingsearch.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public class SearchListingsRequest {
    @DecimalMin(value = "0.0", message = "Minimum price must be zero or greater.")
    private BigDecimal minPrice;
    @DecimalMin(value = "0.0", message = "Maximum price must be zero or greater.")
    private BigDecimal maxPrice;
    @Min(value = 0, message = "Minimum bedrooms must be zero or greater.")
    private Integer minBedrooms;
    private String city;
    private String keyword;
    @DecimalMin(value = "0.0", message = "Target budget must be zero or greater.")
    private BigDecimal targetBudget;
    @Min(value = 0, message = "Page must be zero or greater.")
    private Integer page = 0;
    @Min(value = 1, message = "Page size must be at least 1.")
    @Max(value = 100, message = "Page size cannot exceed 100.")
    private Integer size = 10;

    @AssertTrue(message = "Minimum price must not exceed maximum price.")
    public boolean isPriceRangeValid() {
        return minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) <= 0;
    }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    public Integer getMinBedrooms() { return minBedrooms; }
    public void setMinBedrooms(Integer minBedrooms) { this.minBedrooms = minBedrooms; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public BigDecimal getTargetBudget() { return targetBudget; }
    public void setTargetBudget(BigDecimal targetBudget) { this.targetBudget = targetBudget; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}
