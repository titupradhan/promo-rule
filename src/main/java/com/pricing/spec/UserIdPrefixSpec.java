package com.pricing.spec;

import com.pricing.model.PricingContext;

// Pattern: Specification — checks if the customer's userId starts with the given prefix
public final class UserIdPrefixSpec implements Specification<PricingContext> {

    private final String prefix;

    public UserIdPrefixSpec(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        String userId = ctx.getCustomer().getUserId();
        return userId != null && userId.startsWith(prefix);
    }
}
