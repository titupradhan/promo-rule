package com.pricing.strategy;

import com.pricing.model.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountStrategyTest {

    // Test 6: FlatDiscountStrategy(100) on base ₹500 returns ₹100
    @Test
    void flatDiscount_returnsConfiguredAmount() {
        var strategy = new FlatDiscountStrategy(Money.of(100));
        assertEquals(Money.of(100), strategy.calculate(Money.of(500)));
    }

    // Test 7: PercentageDiscountStrategy(10) on base ₹500 returns ₹50
    @Test
    void percentageDiscount_returnsCorrectPercentage() {
        var strategy = new PercentageDiscountStrategy(10);
        assertEquals(Money.of(50), strategy.calculate(Money.of(500)));
    }

    // Test 8: FlatDiscountStrategy caps at base amount (₹200 flat on ₹150 base → ₹150)
    @Test
    void flatDiscount_capsAtBaseAmount() {
        var strategy = new FlatDiscountStrategy(Money.of(200));
        assertEquals(Money.of(150), strategy.calculate(Money.of(150)));
    }
}
