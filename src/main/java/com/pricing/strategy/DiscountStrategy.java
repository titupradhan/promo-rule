package com.pricing.strategy;

import com.pricing.model.Money;

// Pattern: Strategy — interchangeable discount calculation algorithm
public interface DiscountStrategy {
    Money calculate(Money base);
}
