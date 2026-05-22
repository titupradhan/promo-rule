package com.pricing.spec;

import com.pricing.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecificationsTest {

    private PricingContext ctx(Cart cart, Customer customer) {
        return new PricingContext(cart, customer);
    }

    private Cart cartWithSubtotal(long amount) {
        return new Cart(List.of(
                new Product("P1", "Item", Category.OTHER, Money.of(amount), 1)
        ));
    }

    // Test 1: MinCartValueSpec returns true when subtotal exceeds threshold
    @Test
    void minCartValueSpec_trueWhenSubtotalExceedsThreshold() {
        var spec = new MinCartValueSpec(Money.of(2000));
        assertTrue(spec.isSatisfiedBy(ctx(cartWithSubtotal(2001), new Customer("U1"))));
    }

    // Test 2: UserIdPrefixSpec — matches PREMIUM but not REGULAR
    @Test
    void userIdPrefixSpec_matchesPremiumNotRegular() {
        var spec = new UserIdPrefixSpec("PREMIUM");
        var cart = new Cart(List.of());
        assertTrue(spec.isSatisfiedBy(ctx(cart, new Customer("PREMIUM_123"))));
        assertFalse(spec.isSatisfiedBy(ctx(cart, new Customer("REGULAR_123"))));
    }

    // Test 3: CouponCodeSpec matches correct coupon, false for null/different
    @Test
    void couponCodeSpec_matchesCorrectCoupon() {
        var spec = new CouponCodeSpec("SAVE20");
        Cart withCoupon = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(100), 1)), "SAVE20");
        Cart noCoupon   = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(100), 1)));
        Cart wrongCoupon = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(100), 1)), "SAVE10");
        var customer = new Customer("U1");

        assertTrue(spec.isSatisfiedBy(ctx(withCoupon, customer)));
        assertFalse(spec.isSatisfiedBy(ctx(noCoupon, customer)));
        assertFalse(spec.isSatisfiedBy(ctx(wrongCoupon, customer)));
    }

    // Test 4: HasItemWithQuantityAboveSpec detects item with quantity > 3
    @Test
    void hasItemWithQuantityAboveSpec_detectsBulkItem() {
        var spec = new HasItemWithQuantityAboveSpec(3);
        Cart bulkCart  = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(50), 4)));
        Cart smallCart = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(50), 3)));
        var customer = new Customer("U1");

        assertTrue(spec.isSatisfiedBy(ctx(bulkCart, customer)));
        assertFalse(spec.isSatisfiedBy(ctx(smallCart, customer)));
    }

    // Test 5: Composed spec — categoryPresent.and(minCartValue) only when both hold
    @Test
    void composedSpec_andRequiresBothConditions() {
        var categoryPresent = new CategoryPresenceSpec(Category.ELECTRONICS);
        var minCartValue    = new MinCartValueSpec(Money.of(2000));
        var both = categoryPresent.and(minCartValue);

        Cart electronicsHighValue = new Cart(List.of(
                new Product("P1", "Laptop", Category.ELECTRONICS, Money.of(3000), 1)));
        Cart electronicsLowValue = new Cart(List.of(
                new Product("P1", "Earbuds", Category.ELECTRONICS, Money.of(500), 1)));
        Cart noElectronicsHighValue = new Cart(List.of(
                new Product("P1", "Jacket", Category.CLOTHING, Money.of(3000), 1)));

        var customer = new Customer("U1");

        assertTrue(both.isSatisfiedBy(ctx(electronicsHighValue, customer)));
        assertFalse(both.isSatisfiedBy(ctx(electronicsLowValue, customer)));
        assertFalse(both.isSatisfiedBy(ctx(noElectronicsHighValue, customer)));
    }
}
