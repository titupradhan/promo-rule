package com.pricing.rule;

import com.pricing.model.Money;
import com.pricing.model.PricingContext;
import com.pricing.model.AppliedDiscount;
import com.pricing.spec.Specification;
import com.pricing.strategy.DiscountStrategy;

// Pattern: Chain of Responsibility — abstract base that always forwards (promotions stack)
public abstract class PromotionRule {

    protected String ruleId;
    protected int priority;
    protected Specification<PricingContext> eligibility;
    protected PromotionRule next;

    // Template method: check eligibility → apply → forward to next in chain
    public final void apply(PricingContext ctx) {
        if (eligibility.isSatisfiedBy(ctx)) {
            Money discount = computeDiscount(ctx);
            if (discount.isGreaterThan(Money.ZERO)) {
                ctx.applyDiscount(new AppliedDiscount(ruleId, discount, reason()));
            }
        }
        // Always forward — classical CoR short-circuits; here promotions stack
        if (next != null) next.apply(ctx);
    }

    protected abstract Money computeDiscount(PricingContext ctx);

    protected abstract String reason();

    public PromotionRule setNext(PromotionRule next) {
        this.next = next;
        return next;
    }

    public String getRuleId() { return ruleId; }
    public int getPriority() { return priority; }

    // Pattern: Builder — fluent construction of any PromotionRule subclass
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String id;
        private int priority;
        private Specification<PricingContext> eligibility;
        private DiscountStrategy discountStrategy;
        private AppliesTo appliesTo;
        private Object categoryHint; // only needed by ElectronicsCategoryRule

        public Builder id(String id) { this.id = id; return this; }
        public Builder priority(int priority) { this.priority = priority; return this; }
        public Builder when(Specification<PricingContext> spec) { this.eligibility = spec; return this; }
        public Builder discount(DiscountStrategy strategy) { this.discountStrategy = strategy; return this; }
        public Builder appliesTo(AppliesTo appliesTo) { this.appliesTo = appliesTo; return this; }
        public Builder categoryHint(Object hint) { this.categoryHint = hint; return this; }

        public PromotionRule build() {
            if (id == null || eligibility == null || discountStrategy == null || appliesTo == null) {
                throw new IllegalStateException("id, when, discount, and appliesTo are required");
            }
            return new GenericPromotionRule(id, priority, eligibility, discountStrategy, appliesTo, categoryHint);
        }
    }
}
