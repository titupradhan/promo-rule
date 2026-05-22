package com.pricing;

import com.pricing.engine.PricingEngine;
import com.pricing.model.*;
import com.pricing.repository.InMemoryRuleRepository;

import java.util.List;

public final class Main {

    public static void main(String[] args) {
        PricingEngine engine = new PricingEngine(new InMemoryRuleRepository());

        // ── Scenario 1: Cart > ₹2000 + SAVE20 coupon (CartValueRule + CouponRule stack) ──
        System.out.println("=== Scenario 1: High-value cart with SAVE20 coupon ===");
        Cart cart1 = new Cart(List.of(
                new Product("P1", "Jeans",  Category.CLOTHING,     Money.of(1200), 2),  // ₹2400
                new Product("P2", "T-Shirt", Category.CLOTHING,    Money.of(500),  1)   // ₹500
        ), "SAVE20");
        // subtotal = ₹2900 → CartValueRule (−₹100) + CouponRule (−₹200) = −₹300
        Customer regular = new Customer("USER_42");
        System.out.println(engine.price(cart1, regular));

        // ── Scenario 2: Premium user + Electronics (PremiumUserRule + ElectronicsCategoryRule) ──
        System.out.println("=== Scenario 2: Premium user buying electronics (cart > ₹2000) ===");
        Cart cart2 = new Cart(List.of(
                new Product("P3", "Laptop",  Category.ELECTRONICS, Money.of(6000), 1),  // ₹6000
                new Product("P4", "Mouse",   Category.ELECTRONICS, Money.of(500),  1)   // ₹500
        ));
        // subtotal = ₹6500
        // HighValueItemRule: 5% off Laptop (₹6000) = ₹300
        // ElectronicsCategoryRule: 10% off electronics subtotal (₹6500) = ₹650
        // CartValueRule: ₹100 off (> ₹2000)
        // PremiumUserRule: 15% off running total
        Customer premium = new Customer("PREMIUM_USER_1");
        System.out.println(engine.price(cart2, premium));

        // ── Scenario 3: Bulk purchase (BulkQuantityRule) ──
        System.out.println("=== Scenario 3: Bulk purchase triggers BulkQuantityRule ===");
        Cart cart3 = new Cart(List.of(
                new Product("P5", "Notebook", Category.GROCERY,   Money.of(100),  5),  // qty=5 → bulk
                new Product("P6", "Pen",      Category.OTHER,      Money.of(50),   2)   // qty=2 → no bulk
        ));
        // subtotal = ₹600 (< ₹2000, so CartValueRule won't fire)
        // BulkQuantityRule: ₹50 off (1 qualifying line)
        Customer user = new Customer("USER_99");
        System.out.println(engine.price(cart3, user));
    }
}
