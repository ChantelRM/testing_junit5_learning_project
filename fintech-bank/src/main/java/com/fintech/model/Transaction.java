package com.fintech.model;

import java.time.LocalDateTime;

/**
 * Represents a single transaction on a bank account.
 */
public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT
    }

    private final Type type;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(Type type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f (%s)", timestamp, type, amount, description);
    }
}
