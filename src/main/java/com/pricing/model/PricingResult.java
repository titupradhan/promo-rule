package com.pricing.model;

import java.util.List;

// Immutable output of the PricingEngine
public final class PricingResult {

    private final Money originalTotal;
    private final Money finalTotal;
    private final Money totalSavings;
    private final List<AppliedDiscount> appliedDiscounts;

    public PricingResult(Money originalTotal, Money finalTotal, List<AppliedDiscount> appliedDiscounts) {
        this.originalTotal = originalTotal;
        this.finalTotal = finalTotal;
        this.totalSavings = appliedDiscounts.stream()
                .map(AppliedDiscount::getAmount)
                .reduce(Money.ZERO, Money::add);
        this.appliedDiscounts = List.copyOf(appliedDiscounts);
    }

    public Money getOriginalTotal() { return originalTotal; }
    public Money getFinalTotal() { return finalTotal; }
    public Money getTotalSavings() { return totalSavings; }
    public List<AppliedDiscount> getAppliedDiscounts() { return appliedDiscounts; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Pricing Result ===\n");
        sb.append("  Original : ").append(originalTotal).append("\n");
        if (appliedDiscounts.isEmpty()) {
            sb.append("  No discounts applied.\n");
        } else {
            appliedDiscounts.forEach(d -> sb.append("  ").append(d).append("\n"));
            sb.append("  Savings  : -").append(totalSavings).append("\n");
        }
        sb.append("  Final    : ").append(finalTotal).append("\n");
        return sb.toString();
    }
}
