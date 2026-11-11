package com.primeroute.logistics.entity;

public enum PaymentMethod {
    UPI("UPI"),
    CARD("Credit / Debit Card"),
    NET_BANKING("Net Banking"),
    BANK_TRANSFER("Bank Transfer"),
    COD("Cash on Delivery"),
    CREDIT("Credit / Invoice Billing");

    private final String displayLabel;

    PaymentMethod(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static String getLabelOrFallback(String name) {
        if (name == null || name.isBlank()) return "Not recorded";
        try {
            return PaymentMethod.valueOf(name.trim().toUpperCase()).getDisplayLabel();
        } catch (IllegalArgumentException e) {
            return name;
        }
    }
}
