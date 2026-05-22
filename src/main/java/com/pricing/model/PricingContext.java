package com.pricing.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Mutable context object that flows through the Chain of Responsibility
public final class PricingContext {

    private final Cart cart;
    private final Customer customer;
    private Money runningTotal;
    private final List<AppliedDiscount> appliedDiscounts = new ArrayList<>();

    public PricingContext(Cart cart, Customer customer) {
        this.cart = cart;
        this.customer = customer;
        this.runningTotal = cart.subtotal();
    }

    public Cart getCart() { return cart; }
    public Customer getCustomer() { return customer; }
    public Money getRunningTotal() { return runningTotal; }

    public void applyDiscount(AppliedDiscount discount) {
        appliedDiscounts.add(discount);
        runningTotal = runningTotal.subtract(discount.getAmount());
    }

    public List<AppliedDiscount> getAppliedDiscounts() {
        return Collections.unmodifiableList(appliedDiscounts);
    }
}
