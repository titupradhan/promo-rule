package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.CouponCodeSpec;
import com.pricing.strategy.FlatDiscountStrategy;

// Pattern: Chain of Responsibility — flat ₹200 off when coupon code "SAVE20" is applied
public final class CouponRule extends PromotionRule {

    private static final FlatDiscountStrategy TWO_HUNDRED_OFF = new FlatDiscountStrategy(Money.of(200));

    public CouponRule() {
        this.ruleId = "COUPON_SAVE20";
        this.priority = 60;
        this.eligibility = new CouponCodeSpec("SAVE20");
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        return TWO_HUNDRED_OFF.calculate(ctx.getRunningTotal());
    }

    @Override
    protected String reason() {
        return "Coupon SAVE20: flat ₹200 off";
    }
}
