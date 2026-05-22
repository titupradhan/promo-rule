package com.pricing.spec;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;

// Pattern: Specification — checks if any line item has a unit price above the given threshold
public final class HasItemAbovePriceSpec implements Specification<PricingContext> {

    private final Money threshold;

    public HasItemAbovePriceSpec(Money threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        return ctx.getCart().getProducts().stream()
                .anyMatch(p -> p.getUnitPrice().isGreaterThan(threshold));
    }
}
