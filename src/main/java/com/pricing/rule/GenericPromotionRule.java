package com.pricing.rule;

import com.pricing.model.Category;
import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.Specification;
import com.pricing.strategy.DiscountStrategy;

// Pattern: Chain of Responsibility — general-purpose rule produced by the Builder
final class GenericPromotionRule extends PromotionRule {

    private final DiscountStrategy discountStrategy;
    private final AppliesTo appliesTo;
    private final Object categoryHint; // Category enum value, used only for CATEGORY_SUBTOTAL

    GenericPromotionRule(String ruleId, int priority,
                         Specification<PricingContext> eligibility,
                         DiscountStrategy discountStrategy,
                         AppliesTo appliesTo,
                         Object categoryHint) {
        this.ruleId = ruleId;
        this.priority = priority;
        this.eligibility = eligibility;
        this.discountStrategy = discountStrategy;
        this.appliesTo = appliesTo;
        this.categoryHint = categoryHint;
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        Money base = switch (appliesTo) {
            case CART_TOTAL -> ctx.getRunningTotal();
            case LINE_ITEM -> ctx.getCart().subtotal();
            case CATEGORY_SUBTOTAL -> ctx.getCart().categorySubtotal((Category) categoryHint);
        };
        return discountStrategy.calculate(base);
    }

    @Override
    protected String reason() {
        return ruleId;
    }
}
