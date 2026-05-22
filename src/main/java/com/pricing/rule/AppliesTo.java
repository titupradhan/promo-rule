package com.pricing.rule;

// Enum indicating which base amount a rule's discount strategy operates on
public enum AppliesTo {
    CART_TOTAL,
    LINE_ITEM,
    CATEGORY_SUBTOTAL
}
