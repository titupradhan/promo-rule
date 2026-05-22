package com.pricing.strategy;

import com.pricing.model.Money;

// Pattern: Strategy — deducts a fixed amount, capped at the base (never exceeds what was charged)
public final class FlatDiscountStrategy implements DiscountStrategy {

    private final Money flatAmount;

    public FlatDiscountStrategy(Money flatAmount) {
        this.flatAmount = flatAmount;
    }

    @Override
    public Money calculate(Money base) {
        return flatAmount.min(base);
    }
}
