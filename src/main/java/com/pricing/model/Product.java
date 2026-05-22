package com.pricing.model;

// Domain model — a line item in the cart
public final class Product {

    private final String id;
    private final String name;
    private final Category category;
    private final Money unitPrice;
    private final int quantity;

    public Product(String id, String name, Category category, Money unitPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public Money getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    public Money lineTotal() {
        return unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return name + " x" + quantity + " @ " + unitPrice + " = " + lineTotal();
    }
}
