package com.inv.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Request {
    private int requestId;
    private LocalDate requestDate;
    private String status;          // Pending/Approved/Rejected/Closed
    private Integer orderId;        // nullable
    private int staffId;            // ผู้ยื่น
    private String description;
    private Integer approvedBy;     // staff_id
    private LocalDateTime approvedDate;

    private List<RequestItem> items; // service เติมให้

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }

    public List<RequestItem> getItems() { return items; }
    public void setItems(List<RequestItem> items) { this.items = items; }
}
