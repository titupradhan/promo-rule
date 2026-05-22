# Composable Pricing Engine

A Java 17 pricing engine with **chainable promotion rules**, built to demonstrate clean OOP and GoF design patterns.

## Design Patterns

| Pattern | Where Used | Purpose |
|---|---|---|
| **Chain of Responsibility** | `PromotionRule` chain | Rules apply in priority order; promotions stack (always-forward, not short-circuit) |
| **Strategy** | `DiscountStrategy` | `FlatDiscountStrategy` and `PercentageDiscountStrategy` are interchangeable |
| **Specification** | `Specification<PricingContext>` | Composable `and/or/not` eligibility predicates, unit-testable in isolation |
| **Builder** | `PromotionRule.Builder` | Fluent rule construction |
| **Factory** | `InMemoryRuleRepository` | Builds the 6 sample rules; swap for DB/JSON repo without changing the engine |

## Promotion Rules

| Priority | Rule | Condition | Discount |
|---|---|---|---|
| 10 | `BulkQuantityRule` | Any item with `quantity > 3` | ₹50 off per qualifying line |
| 20 | `HighValueItemRule` | Any item with `unitPrice > ₹5000` | 5% off that item's subtotal |
| 30 | `ElectronicsCategoryRule` | Cart contains `ELECTRONICS` items | 10% off electronics subtotal |
| 40 | `CartValueRule` | Cart subtotal `> ₹2000` | Flat ₹100 off |
| 50 | `PremiumUserRule` | `userId` starts with `"PREMIUM"` | 15% off running total |
| 60 | `CouponRule` | Coupon code `"SAVE20"` applied | Flat ₹200 off |

All rules stack — multiple discounts apply in priority order.

## Project Structure

```
src/
  main/java/com/pricing/
    model/        # Money, Product, Cart, Customer, Category, PricingContext, PricingResult
    spec/         # Specification<T> + 6 concrete specs
    strategy/     # DiscountStrategy, FlatDiscountStrategy, PercentageDiscountStrategy
    rule/         # PromotionRule (abstract + Builder) + 6 concrete rules
    repository/   # RuleRepository interface + InMemoryRuleRepository
    engine/       # PricingEngine
    Main.java     # 3 demo scenarios
  test/java/com/pricing/
    spec/         # SpecificationsTest (5 tests)
    strategy/     # DiscountStrategyTest (3 tests)
    rule/         # RulesTest (4 tests)
    engine/       # PricingEngineTest (5 tests)
```

## Build & Run

```bash
# Run all tests
mvn clean test

# Run demo scenarios
mvn exec:java -Dexec.mainClass="com.pricing.Main"
```

**Requirements:** Java 17+, Maven 3.x

## Demo Output

```
=== Scenario 1: High-value cart with SAVE20 coupon ===
=== Pricing Result ===
  Original : ₹2900.00
  [CART_VALUE_100] Cart value > ₹2000: flat ₹100 off → -₹100.00
  [COUPON_SAVE20] Coupon SAVE20: flat ₹200 off → -₹200.00
  Savings  : -₹300.00
  Final    : ₹2600.00

=== Scenario 2: Premium user buying electronics (cart > ₹2000) ===
=== Pricing Result ===
  Original : ₹6500.00
  [HIGH_VALUE_5PCT] High-value item: 5% off items with unit price > ₹5000 → -₹300.00
  [ELECTRONICS_10PCT] Electronics category: 10% off electronics subtotal → -₹650.00
  [CART_VALUE_100] Cart value > ₹2000: flat ₹100 off → -₹100.00
  [PREMIUM_USER_15] Premium user: 15% off cart total → -₹817.50
  Savings  : -₹1867.50
  Final    : ₹4632.50

=== Scenario 3: Bulk purchase triggers BulkQuantityRule ===
=== Pricing Result ===
  Original : ₹600.00
  [BULK_QTY_50] Bulk discount: ₹50 off per item with qty > 3 → -₹50.00
  Savings  : -₹50.00
  Final    : ₹550.00
```

## Extending the Engine

- **New discount type** — add one class implementing `DiscountStrategy`. Zero changes elsewhere.
- **New eligibility condition** — add one class implementing `Specification<PricingContext>`.
- **New rule** — extend `PromotionRule`, wire spec + strategy, register in the repository.
- **Dynamic rule loading** — swap `InMemoryRuleRepository` for a `DbRuleRepository` or `JsonRuleRepository`. The engine is unchanged.
