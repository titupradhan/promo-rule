package com.pricing.model;

import java.util.List;

// Domain model — the shopping cart
public final class Cart {

    private final List<Product> products;
    private final String couponCode; // nullable

    public Cart(List<Product> products, String couponCode) {
        this.products = List.copyOf(products);
        this.couponCode = couponCode;
    }

    public Cart(List<Product> products) {
        this(products, null);
    }

    public List<Product> getProducts() { return products; }
    public String getCouponCode() { return couponCode; }

    public Money subtotal() {
        return products.stream()
                .map(Product::lineTotal)
                .reduce(Money.ZERO, Money::add);
    }

    public Money categorySubtotal(Category category) {
        return products.stream()
                .filter(p -> p.getCategory() == category)
                .map(Product::lineTotal)
                .reduce(Money.ZERO, Money::add);
    }

    @Override
    public String toString() {
        return "Cart{items=" + products.size() + ", subtotal=" + subtotal()
                + ", coupon=" + couponCode + "}";
    }
}
