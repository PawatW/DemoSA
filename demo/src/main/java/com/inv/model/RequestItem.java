package com.inv.model;

public class RequestItem {
    private int requestItemId;
    private int requestId;
    private int productId;
    private int quantity;       // requested
    private int fulfilledQty;   // default 0
    private int remainingQty;   // default = quantity at create

    public int getRequestItemId() { return requestItemId; }
    public void setRequestItemId(int requestItemId) { this.requestItemId = requestItemId; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getFulfilledQty() { return fulfilledQty; }
    public void setFulfilledQty(int fulfilledQty) { this.fulfilledQty = fulfilledQty; }

    public int getRemainingQty() { return remainingQty; }
    public void setRemainingQty(int remainingQty) { this.remainingQty = remainingQty; }
}
