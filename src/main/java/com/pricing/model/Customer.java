package com.pricing.model;

// Domain model — represents the user placing the order
public final class Customer {

    private final String userId;

    public Customer(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Customer{userId='" + userId + "'}";
    }
}
