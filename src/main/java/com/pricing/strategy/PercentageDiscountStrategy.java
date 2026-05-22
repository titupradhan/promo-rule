package com.pricing.strategy;

import com.pricing.model.Money;

import java.math.BigDecimal;

// Pattern: Strategy — deducts a percentage of the base amount
public final class PercentageDiscountStrategy implements DiscountStrategy {

    private final BigDecimal percentage; // e.g. 15 means 15%

    public PercentageDiscountStrategy(int percentage) {
        this.percentage = BigDecimal.valueOf(percentage);
    }

    @Override
    public Money calculate(Money base) {
        return base.multiply(percentage.divide(BigDecimal.valueOf(100)));
    }
}
