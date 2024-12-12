package com.example.financetracker;

public class Transaction {
    private double amount;
    private String category;
    private long timestamp;

    // Constructor
    public Transaction(double amount, String category, long timestamp) {
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
    }

    // Getter methods
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
