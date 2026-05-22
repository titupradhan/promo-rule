package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.HasItemWithQuantityAboveSpec;
import com.pricing.strategy.FlatDiscountStrategy;

// Pattern: Chain of Responsibility — flat ₹50 off per qualifying line item (quantity > 3)
public final class BulkQuantityRule extends PromotionRule {

    private static final Money DISCOUNT_PER_ITEM = Money.of(50);
    private final FlatDiscountStrategy strategy = new FlatDiscountStrategy(DISCOUNT_PER_ITEM);

    public BulkQuantityRule() {
        this.ruleId = "BULK_QTY_50";
        this.priority = 10;
        this.eligibility = new HasItemWithQuantityAboveSpec(3);
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        // Apply ₹50 discount for each qualifying line item
        long qualifyingLines = ctx.getCart().getProducts().stream()
                .filter(p -> p.getQuantity() > 3)
                .count();
        return Money.of(50 * qualifyingLines);
    }

    @Override
    protected String reason() {
        return "Bulk discount: ₹50 off per item with qty > 3";
    }
}
