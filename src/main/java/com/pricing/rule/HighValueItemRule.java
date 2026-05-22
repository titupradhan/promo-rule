package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.HasItemAbovePriceSpec;
import com.pricing.strategy.PercentageDiscountStrategy;

// Pattern: Chain of Responsibility — 5% off line items where unit price > ₹5000
public final class HighValueItemRule extends PromotionRule {

    private static final PercentageDiscountStrategy FIVE_PERCENT = new PercentageDiscountStrategy(5);

    public HighValueItemRule() {
        this.ruleId = "HIGH_VALUE_5PCT";
        this.priority = 20;
        this.eligibility = new HasItemAbovePriceSpec(Money.of(5000));
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        // 5% off the subtotal of qualifying high-value items
        Money highValueSubtotal = ctx.getCart().getProducts().stream()
                .filter(p -> p.getUnitPrice().isGreaterThan(Money.of(5000)))
                .map(p -> p.lineTotal())
                .reduce(Money.ZERO, Money::add);
        return FIVE_PERCENT.calculate(highValueSubtotal);
    }

    @Override
    protected String reason() {
        return "High-value item: 5% off items with unit price > ₹5000";
    }
}
