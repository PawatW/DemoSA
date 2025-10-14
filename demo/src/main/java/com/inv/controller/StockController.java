package com.inv.controller;

import com.inv.model.Request;
import com.inv.model.StockTransaction;
import com.inv.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/in")
    public void addStockIn(@RequestBody StockInRequest request, Principal principal) {
        int staffId = Integer.parseInt(principal.getName());

        stockService.addStockIn(
                request.getProductId(),
                request.getQuantity(),
                staffId,
                request.getSupplierId(),
                request.getNote()
        );
    }

    @GetMapping("/transactions")
    public List<StockTransaction> getAllTransactions() {
        return stockService.getAllTransactions();
    }
    // --- Fulfillment / Stock-Out (ของใหม่) ---
    @GetMapping("/approved-requests")
    public List<Request> getApprovedRequests() {
        return stockService.getApprovedRequests();
    }

    @PostMapping("/fulfill")
    public void fulfillItem(@RequestBody FulfillRequest request, Principal principal) {
        int warehouseStaffId = Integer.parseInt(principal.getName());
        stockService.fulfillItem(request.getRequestItemId(), request.getFulfillQty(), warehouseStaffId);
    }

    // Inner class สำหรับรับ JSON request ของการเบิกของ
    public static class FulfillRequest {
        private int requestItemId;
        private int fulfillQty;

        // Getters and Setters
        public int getRequestItemId() { return requestItemId; }
        public void setRequestItemId(int requestItemId) { this.requestItemId = requestItemId; }
        public int getFulfillQty() { return fulfillQty; }
        public void setFulfillQty(int fulfillQty) { this.fulfillQty = fulfillQty; }
    }


    // Inner class สำหรับรับ JSON request
    public static class StockInRequest {
        private int productId;
        private int quantity;
        private int supplierId;
        private String note;

        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public int getSupplierId() { return supplierId; }
        public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }
}