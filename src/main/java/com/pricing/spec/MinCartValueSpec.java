package com.pricing.spec;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;

// Pattern: Specification — checks if cart subtotal exceeds the given threshold
public final class MinCartValueSpec implements Specification<PricingContext> {

    private final Money threshold;

    public MinCartValueSpec(Money threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        return ctx.getCart().subtotal().isGreaterThan(threshold);
    }
}
