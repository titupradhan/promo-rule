package com.pricing.rule;

import com.pricing.model.Category;
import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.CategoryPresenceSpec;
import com.pricing.strategy.PercentageDiscountStrategy;

// Pattern: Chain of Responsibility — 10% off electronics subtotal when cart has ELECTRONICS items
public final class ElectronicsCategoryRule extends PromotionRule {

    private static final PercentageDiscountStrategy TEN_PERCENT = new PercentageDiscountStrategy(10);

    public ElectronicsCategoryRule() {
        this.ruleId = "ELECTRONICS_10PCT";
        this.priority = 30;
        this.eligibility = new CategoryPresenceSpec(Category.ELECTRONICS);
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        Money electronicsSubtotal = ctx.getCart().categorySubtotal(Category.ELECTRONICS);
        return TEN_PERCENT.calculate(electronicsSubtotal);
    }

    @Override
    protected String reason() {
        return "Electronics category: 10% off electronics subtotal";
    }
}
