package com.pricing.rule;

import com.pricing.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    private PricingContext makeCtx(Cart cart, Customer customer) {
        return new PricingContext(cart, customer);
    }

    // Test 9: BulkQuantityRule applies ₹50 off when an item has quantity 4
    @Test
    void bulkQuantityRule_appliesWhenQtyAbove3() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Pen", Category.OTHER, Money.of(50), 4)
        ));
        var ctx = makeCtx(cart, new Customer("U1"));
        new BulkQuantityRule().apply(ctx);

        assertEquals(1, ctx.getAppliedDiscounts().size());
        assertEquals(Money.of(50), ctx.getAppliedDiscounts().get(0).getAmount());
    }

    // Test 10: BulkQuantityRule does NOT apply when all items have quantity ≤ 3
    @Test
    void bulkQuantityRule_doesNotApplyWhenQtyAtOrBelow3() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Pen", Category.OTHER, Money.of(50), 3)
        ));
        var ctx = makeCtx(cart, new Customer("U1"));
        new BulkQuantityRule().apply(ctx);

        assertTrue(ctx.getAppliedDiscounts().isEmpty());
    }

    // Test 11: PremiumUserRule applies for PREMIUM_USER_1, not for USER_1
    @Test
    void premiumUserRule_appliesOnlyForPremiumUsers() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Item", Category.OTHER, Money.of(1000), 1)
        ));

        var premiumCtx = makeCtx(cart, new Customer("PREMIUM_USER_1"));
        new PremiumUserRule().apply(premiumCtx);
        assertEquals(1, premiumCtx.getAppliedDiscounts().size());

        var regularCtx = makeCtx(cart, new Customer("USER_1"));
        new PremiumUserRule().apply(regularCtx);
        assertTrue(regularCtx.getAppliedDiscounts().isEmpty());
    }

    // Test 12: CouponRule applies ₹200 off when coupon SAVE20 is present
    @Test
    void couponRule_appliesWith_SAVE20() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Item", Category.OTHER, Money.of(1000), 1)
        ), "SAVE20");
        var ctx = makeCtx(cart, new Customer("U1"));
        new CouponRule().apply(ctx);

        assertEquals(1, ctx.getAppliedDiscounts().size());
        assertEquals(Money.of(200), ctx.getAppliedDiscounts().get(0).getAmount());
    }
}
