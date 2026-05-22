package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.spec.MinCartValueSpec;
import com.pricing.strategy.FlatDiscountStrategy;

// Pattern: Chain of Responsibility — flat ₹100 off when cart subtotal exceeds ₹2000
public final class CartValueRule extends PromotionRule {

    private static final FlatDiscountStrategy HUNDRED_OFF = new FlatDiscountStrategy(Money.of(100));

    public CartValueRule() {
        this.ruleId = "CART_VALUE_100";
        this.priority = 40;
        this.eligibility = new MinCartValueSpec(Money.of(2000));
    }

    @Override
    protected Money computeDiscount(PricingContext ctx) {
        return HUNDRED_OFF.calculate(ctx.getRunningTotal());
    }

    @Override
    protected String reason() {
        return "Cart value > ₹2000: flat ₹100 off";
    }
}
