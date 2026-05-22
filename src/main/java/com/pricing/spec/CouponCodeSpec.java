package com.pricing.spec;

import com.pricing.model.PricingContext;

// Pattern: Specification — checks if the cart has a specific coupon code applied
public final class CouponCodeSpec implements Specification<PricingContext> {

    private final String expectedCode;

    public CouponCodeSpec(String expectedCode) {
        this.expectedCode = expectedCode;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        return expectedCode.equalsIgnoreCase(ctx.getCart().getCouponCode());
    }
}
