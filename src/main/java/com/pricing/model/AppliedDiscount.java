package com.pricing.model;

// Value object — records one discount that was applied during pricing
public final class AppliedDiscount {

    private final String ruleId;
    private final Money amount;
    private final String reason;

    public AppliedDiscount(String ruleId, Money amount, String reason) {
        this.ruleId = ruleId;
        this.amount = amount;
        this.reason = reason;
    }

    public String getRuleId() { return ruleId; }
    public Money getAmount() { return amount; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "[" + ruleId + "] " + reason + " → -" + amount;
    }
}
