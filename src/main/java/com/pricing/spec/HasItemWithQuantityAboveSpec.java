package com.pricing.spec;

import com.pricing.model.PricingContext;

// Pattern: Specification — checks if any line item has quantity strictly above the given threshold
public final class HasItemWithQuantityAboveSpec implements Specification<PricingContext> {

    private final int threshold;

    public HasItemWithQuantityAboveSpec(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        return ctx.getCart().getProducts().stream()
                .anyMatch(p -> p.getQuantity() > threshold);
    }
}
