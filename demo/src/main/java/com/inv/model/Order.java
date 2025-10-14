package com.inv.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Order {
    private int orderId;
    private LocalDate orderDate;
    private BigDecimal totalAmount;
    private String status;          // Confirmed, Completed
    private int customerId;
    private int staffId;
    private List<OrderItem> items;  // not persisted automatically; service จะไปดึงแยก

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
