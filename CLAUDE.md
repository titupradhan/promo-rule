# Composable Pricing Engine — LLD (45–60 min Interview Scope)

## Goal

Build a **Composable Pricing Engine** in Java with **chainable promotion rules**. Focus on clean OOP and correct use of GoF patterns. Keep it small, demonstrable, and well-tested.

Pure Java 17+. JUnit 5. **No Spring, no DB, no JSON parsing** — rules live in an in-memory repository for now (mention in code comments how this would map to DB/JSON later).

---

## Scope — Exactly These Rules

Implement exactly **6 rules**. Nothing more.

### Item-level (2 rules)
1. **BulkQuantityRule** — If any single item in the cart has `quantity > 3`, give a **flat ₹50 off** that line item.
2. **HighValueItemRule** — If any single item has `unitPrice > 5000`, give **5% off** that line item.

### Category-level (1 rule)
3. **ElectronicsCategoryRule** — If cart contains any item in `ELECTRONICS` category, give **10% off** the electronics subtotal.

### Cart-level (1 rule)
4. **CartValueRule** — If cart subtotal > ₹2000, give **flat ₹100 off** the cart.

### User-level (1 rule)
5. **PremiumUserRule** — If `userId` starts with `"PREMIUM"`, give **15% off** the cart total.

### Coupon (1 rule)
6. **CouponRule** — If coupon code `"SAVE20"` is applied, give **flat ₹200 off** the cart.

---

## Required Design Patterns

| Pattern | Where | Why |
|---|---|---|
| **Chain of Responsibility** | `PromotionRule` chain | Each rule applies and forwards context to the next |
| **Strategy** | `DiscountStrategy` | `FlatDiscountStrategy` and `PercentageDiscountStrategy` are interchangeable |
| **Specification** | `Specification<PricingContext>` | Composable eligibility predicates |
| **Builder** | `PromotionRule.Builder` | Fluent rule construction |
| **Factory** *(light)* | `PromotionRuleFactory` | Builds the 6 sample rules for the in-memory repo |

Add a one-line comment at the top of each class naming the pattern it represents.

---

## Domain Model

```java
Money               // BigDecimal-backed, currency-safe arithmetic
Product             // id, name, category (enum), unitPrice, quantity
Cart                // List<Product>, couponCode (nullable), subtotal()
Customer            // userId
Category            // enum: ELECTRONICS, CLOTHING, GROCERY, OTHER

PricingContext      // cart, customer, runningTotal (mutable), List<AppliedDiscount>
AppliedDiscount     // ruleId, amount, reason
PricingResult       // originalTotal, finalTotal, totalSavings, List<AppliedDiscount>
```

`PricingContext` is mutated as it flows through the chain. `PricingResult` is the immutable output.

---

## Key Interfaces

```java
interface Specification<T> {
    boolean isSatisfiedBy(T ctx);
    default Specification<T> and(Specification<T> other) { ... }
    default Specification<T> or(Specification<T> other) { ... }
    default Specification<T> not() { ... }
}

interface DiscountStrategy {
    Money calculate(Money base);   // base = the amount the discount applies to
}

abstract class PromotionRule {
    protected String ruleId;
    protected int priority;
    protected Specification<PricingContext> eligibility;
    protected PromotionRule next;

    // Template method — always forwards (stacking is allowed for this scope)
    public final void apply(PricingContext ctx) {
        if (eligibility.isSatisfiedBy(ctx)) {
            Money discount = computeDiscount(ctx);
            ctx.applyDiscount(new AppliedDiscount(ruleId, discount, reason()));
        }
        if (next != null) next.apply(ctx);
    }

    protected abstract Money computeDiscount(PricingContext ctx);
    protected abstract String reason();

    public PromotionRule setNext(PromotionRule next) { this.next = next; return next; }
}
```

**Talking point for interviewer:** Classical Chain of Responsibility short-circuits on first match. Here, we always forward because promotions stack. A `stackable` flag could be added later to support non-stacking rules.

---

## Required Components

### `model/`
- `Money`, `Product`, `Cart`, `Customer`, `Category` (enum), `PricingContext`, `AppliedDiscount`, `PricingResult`

### `spec/`
- `Specification<T>` (interface, with default `and`/`or`/`not`)
- `MinCartValueSpec` — used by `CartValueRule`
- `CategoryPresenceSpec` — used by `ElectronicsCategoryRule`
- `UserIdPrefixSpec` — used by `PremiumUserRule`
- `CouponCodeSpec` — used by `CouponRule`
- `HasItemWithQuantityAboveSpec` — used by `BulkQuantityRule`
- `HasItemAbovePriceSpec` — used by `HighValueItemRule`

### `strategy/`
- `DiscountStrategy` (interface)
- `FlatDiscountStrategy`
- `PercentageDiscountStrategy`

### `rule/`
- `PromotionRule` (abstract base — Chain of Responsibility)
- Six concrete rules, each in its own file:
    - `BulkQuantityRule`
    - `HighValueItemRule`
    - `ElectronicsCategoryRule`
    - `CartValueRule`
    - `PremiumUserRule`
    - `CouponRule`
- `PromotionRule.Builder` — fluent builder

### `repository/`
- `RuleRepository` (interface) — `List<PromotionRule> loadAll()`
- `InMemoryRuleRepository` — returns the 6 rules in priority order
    - Add a Javadoc comment: *"In production, this would be backed by a DB table or JSON config loaded at startup."*

### `engine/`
- `PricingEngine`
    - Constructor: `PricingEngine(RuleRepository repo)`
    - Method: `PricingResult price(Cart cart, Customer customer)`
    - Loads rules → sorts by priority → builds chain → applies → returns `PricingResult`

### `Main.java`
- 3 demo scenarios showing different rule combinations, prints `PricingResult` for each.

---

## Suggested Rule Priorities

Apply item/category discounts first, then cart-level, then user/coupon. This is the most intuitive order.

| Priority | Rule |
|---|---|
| 10 | BulkQuantityRule |
| 20 | HighValueItemRule |
| 30 | ElectronicsCategoryRule |
| 40 | CartValueRule |
| 50 | PremiumUserRule |
| 60 | CouponRule |

---

## Example Builder Usage

```java
PromotionRule premiumRule = PromotionRule.builder()
    .id("PREMIUM_USER_15")
    .priority(50)
    .when(new UserIdPrefixSpec("PREMIUM"))
    .discount(new PercentageDiscountStrategy(15))
    .appliesTo(AppliesTo.CART_TOTAL)
    .build();
```

`AppliesTo` is a simple enum (`CART_TOTAL`, `LINE_ITEM`, `CATEGORY_SUBTOTAL`) the rule uses to determine what base amount to pass to the strategy.

---

## Project Structure

```
src/
  main/java/com/pricing/
    model/
    spec/
    strategy/
    rule/
    repository/
    engine/
    Main.java
  test/java/com/pricing/
    spec/SpecificationsTest.java
    strategy/DiscountStrategyTest.java
    rule/RulesTest.java
    engine/PricingEngineTest.java
```

---

## JUnit 5 Test Cases (Required)

Keep tests focused. Don't over-test — these are enough to demonstrate correctness.

### `SpecificationsTest`
1. `MinCartValueSpec` returns true when subtotal exceeds threshold.
2. `UserIdPrefixSpec` matches "PREMIUM_123" but not "REGULAR_123".
3. `CouponCodeSpec` matches when correct coupon is applied; false when null/different.
4. `HasItemWithQuantityAboveSpec` detects an item with quantity > 3.
5. Composition: `categoryPresent.and(minCartValue)` returns true only when both hold.

### `DiscountStrategyTest`
6. `FlatDiscountStrategy(100)` on base ₹500 returns ₹100.
7. `PercentageDiscountStrategy(10)` on base ₹500 returns ₹50.
8. `FlatDiscountStrategy` caps at base amount (₹200 flat on ₹150 base → ₹150, not ₹200).

### `RulesTest`
9. `BulkQuantityRule` applies ₹50 off when an item has quantity 4.
10. `BulkQuantityRule` does NOT apply when all items have quantity ≤ 3.
11. `PremiumUserRule` applies for userId "PREMIUM_USER_1", not for "USER_1".
12. `CouponRule` applies ₹200 off when coupon "SAVE20" is present.

### `PricingEngineTest` (integration)
13. Empty applicable rules → final total = subtotal.
14. Premium user + electronics in cart + cart > ₹2000 → all three rules apply in priority order.
15. Coupon + cart-value rule both apply (stacking works).
16. `PricingResult.appliedDiscounts` contains the correct ruleIds in priority order.
17. Total savings = sum of all applied discount amounts.

---

## Build

```bash
mvn clean test
mvn exec:java -Dexec.mainClass="com.pricing.Main"
```

Minimal `pom.xml` — only `junit-jupiter` dependency.

---

## What to Skip (out of scope for this interview)

- JSON/DB loading (mention how it would plug in via `RuleRepository` — that's enough)
- Composite/bundled rules
- Stackability flags / conflict resolution strategies
- Time-windowed rules
- Decorator, Observer, complex Factory-from-JSON
- BOGO, tiered, free shipping strategies

---

## Talking Points (have these ready for the interviewer)

1. **Why Chain of Responsibility?** Rules are independent and order-sensitive. New rules plug in without touching existing ones.
2. **Why always-forward instead of short-circuit?** Promotions stack. Adding a `stackable` flag is a 5-minute extension.
3. **Why Strategy for discounts?** Flat vs percentage are interchangeable algorithms. Adding "tiered" later = one new class, zero changes elsewhere.
4. **Why Specification?** Eligibility logic gets complex (AND/OR/NOT compositions). Specs are reusable across rules and unit-testable in isolation.
5. **How would dynamic authoring work?** Swap `InMemoryRuleRepository` for a `JsonRuleRepository` or `DbRuleRepository`. Add a `RuleFactory` that converts JSON → `PromotionRule` via `Specification` and `Strategy` factories. The engine doesn't change.
6. **Open/Closed in action:** Adding a new discount type = one new `DiscountStrategy` class. Adding a new condition = one new `Specification`. The engine is closed for modification, open for extension.
7. **Why BigDecimal for money?** Floating-point rounding errors are unacceptable for currency.

---

## Deliverables

1. Compilable Java under `src/main/java/`
2. JUnit 5 tests under `src/test/java/` covering the cases above
3. `Main.java` with 3 demo scenarios printing `PricingResult`
4. Minimal `pom.xml`

Keep total code under ~800 lines. This is an interview, not a product.