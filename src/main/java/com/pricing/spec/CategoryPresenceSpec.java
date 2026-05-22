package com.pricing.spec;

import com.pricing.model.Category;
import com.pricing.model.PricingContext;

// Pattern: Specification — checks if cart contains at least one item of the given category
public final class CategoryPresenceSpec implements Specification<PricingContext> {

    private final Category category;

    public CategoryPresenceSpec(Category category) {
        this.category = category;
    }

    @Override
    public boolean isSatisfiedBy(PricingContext ctx) {
        return ctx.getCart().getProducts().stream()
                .anyMatch(p -> p.getCategory() == category);
    }
}
