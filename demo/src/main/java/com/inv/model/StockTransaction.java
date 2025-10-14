package com.inv.model;

import java.time.LocalDateTime;

public class StockTransaction {
    private int transactionId;
    private LocalDateTime transactionDate;
    private String type; // 'IN', 'OUT', 'ADJUST'
    private int productId;
    private int quantity;
    private int staffId;
    private String reference; // ใช้แทน note ใน use case

    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}