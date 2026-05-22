package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.UserIdPrefixSpec;
import com.pricing.strategy.PercentageDiscountStrategy;

// Pattern: Chain of Responsibility — 15% off cart total for PREMIUM users
public final class PremiumUserRule extends PromotionRule {

    private static final PercentageDiscountStrategy FIFTEEN_PERCENT = new PercentageDiscountStrategy(15);

    public PremiumUserRule() {
        this.ruleId = "PREMIUM_USER_15";
        this.priority = 50;
        this.eligibility = new UserIdPrefixSpec("PREMIUM");
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        return FIFTEEN_PERCENT.calculate(ctx.getRunningTotal());
    }

    @Override
    protected String reason() {
        return "Premium user: 15% off cart total";
    }
}
