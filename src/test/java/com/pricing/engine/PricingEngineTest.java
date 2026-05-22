package com.pricing.engine;

import com.pricing.model.*;
import com.pricing.repository.InMemoryRuleRepository;
import com.pricing.repository.RuleRepository;
import com.pricing.rule.PromotionRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PricingEngineTest {

    private final PricingEngine engine = new PricingEngine(new InMemoryRuleRepository());

    // Test 13: Empty applicable rules → final total = subtotal
    @Test
    void noApplicableRules_finalTotalEqualsSubtotal() {
        PricingEngine emptyEngine = new PricingEngine(List::of);
        Cart cart = new Cart(List.of(new Product("P1", "Item", Category.OTHER, Money.of(500), 1)));
        PricingResult result = emptyEngine.price(cart, new Customer("U1"));

        assertEquals(Money.of(500), result.getOriginalTotal());
        assertEquals(Money.of(500), result.getFinalTotal());
        assertTrue(result.getAppliedDiscounts().isEmpty());
    }

    // Test 14: Premium user + electronics + cart > ₹2000 → multiple rules fire
    @Test
    void premiumUser_electronics_highCart_allApplicableRulesFire() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Laptop", Category.ELECTRONICS, Money.of(6000), 1)
        ));
        PricingResult result = engine.price(cart, new Customer("PREMIUM_USER_1"));

        // HighValueItemRule, ElectronicsCategoryRule, CartValueRule, PremiumUserRule should all fire
        List<String> ruleIds = result.getAppliedDiscounts().stream()
                .map(AppliedDiscount::getRuleId).toList();
        assertTrue(ruleIds.contains("HIGH_VALUE_5PCT"));
        assertTrue(ruleIds.contains("ELECTRONICS_10PCT"));
        assertTrue(ruleIds.contains("CART_VALUE_100"));
        assertTrue(ruleIds.contains("PREMIUM_USER_15"));
        assertTrue(result.getFinalTotal().getAmount().compareTo(result.getOriginalTotal().getAmount()) < 0);
    }

    // Test 15: Coupon + cart-value rule both apply (stacking works)
    @Test
    void couponAndCartValueRule_bothStack() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Jacket", Category.CLOTHING, Money.of(2500), 1)
        ), "SAVE20");
        PricingResult result = engine.price(cart, new Customer("USER_1"));

        List<String> ruleIds = result.getAppliedDiscounts().stream()
                .map(AppliedDiscount::getRuleId).toList();
        assertTrue(ruleIds.contains("CART_VALUE_100"));
        assertTrue(ruleIds.contains("COUPON_SAVE20"));
        // ₹2500 − ₹100 − ₹200 = ₹2200
        assertEquals(Money.of(2200), result.getFinalTotal());
    }

    // Test 16: appliedDiscounts list contains correct ruleIds in priority order
    @Test
    void appliedDiscounts_inPriorityOrder() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Laptop", Category.ELECTRONICS, Money.of(6000), 1)
        ), "SAVE20");
        PricingResult result = engine.price(cart, new Customer("PREMIUM_USER_1"));

        List<String> ruleIds = result.getAppliedDiscounts().stream()
                .map(AppliedDiscount::getRuleId).toList();

        // Rules should fire and be recorded in priority order (10, 20, 30, 40, 50, 60)
        // Not all 6 may fire but the ones that do must be in ascending priority order
        int lastPriority = -1;
        for (String id : ruleIds) {
            int p = priorityOf(id);
            assertTrue(p > lastPriority, "Rules out of priority order: " + ruleIds);
            lastPriority = p;
        }
    }

    // Test 17: totalSavings = sum of all applied discount amounts
    @Test
    void totalSavings_equalsSumOfAppliedDiscounts() {
        Cart cart = new Cart(List.of(
                new Product("P1", "Jacket", Category.CLOTHING, Money.of(2500), 1)
        ), "SAVE20");
        PricingResult result = engine.price(cart, new Customer("USER_1"));

        Money expectedSavings = result.getAppliedDiscounts().stream()
                .map(AppliedDiscount::getAmount)
                .reduce(Money.ZERO, Money::add);
        assertEquals(expectedSavings, result.getTotalSavings());
    }

    private int priorityOf(String ruleId) {
        return switch (ruleId) {
            case "BULK_QTY_50"       -> 10;
            case "HIGH_VALUE_5PCT"   -> 20;
            case "ELECTRONICS_10PCT" -> 30;
            case "CART_VALUE_100"    -> 40;
            case "PREMIUM_USER_15"   -> 50;
            case "COUPON_SAVE20"     -> 60;
            default -> throw new IllegalArgumentException("Unknown ruleId: " + ruleId);
        };
    }
}
