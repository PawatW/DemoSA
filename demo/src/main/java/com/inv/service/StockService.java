package com.inv.service;

import com.inv.model.Product;
import com.inv.model.Request;
import com.inv.model.RequestItem;
import com.inv.model.StockTransaction;
import com.inv.repo.OrderRepository;
import com.inv.repo.ProductRepository;
import com.inv.repo.RequestRepository;
import com.inv.repo.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
public class StockService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockTransactionRepository stockTransactionRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Transactional // สำคัญมาก: ทำให้มั่นใจว่าถ้าเกิด Error ระหว่างทาง, การทำงานทั้งหมดจะถูกยกเลิก
    public void addStockIn(int productId, int quantity, int staffId, int supplierId, String note) {
        // 1. Update Stock Quantity
        productRepository.updateQuantity(productId, quantity);

        // 2. Record Stock Transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setType("IN");
        transaction.setProductId(productId);
        transaction.setQuantity(quantity);
        transaction.setStaffId(staffId);

        // สร้าง reference note ตาม use case
        String referenceNote = String.format("Stock-In from Supplier ID %d. Note: %s", supplierId, note);
        transaction.setReference(referenceNote);

        stockTransactionRepository.save(transaction);
    }

    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository.findAll();
    }
    public List<Request> getApprovedRequests() {
        return requestRepository.findApprovedRequests();
    }

    // เพิ่ม: Method หลักสำหรับกระบวนการเบิกของ
    @Transactional
    public void fulfillItem(int requestItemId, int fulfillQty, int warehouseStaffId) {
        // 9. Validation
        if (fulfillQty <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "จำนวนที่เบิกต้องมากกว่า 0");
        }
        RequestItem item = requestRepository.findItemById(requestItemId);
        if (item == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ไม่พบรายการเบิกที่ระบุ");
        }
        if (fulfillQty > item.getRemainingQty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "จำนวนที่เบิกเกินกว่าที่เหลืออยู่");
        }
        Product product = productRepository.findById(item.getProductId());
        if (product.getQuantity() < fulfillQty) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "สินค้าในคลังไม่เพียงพอ");
        }

        // 11. Update Fulfillment
        // ① Update RequestItem
        requestRepository.updateItemFulfillment(requestItemId, fulfillQty);

        // ② Update Stock ใน Product (ส่งค่าติดลบ)
        productRepository.updateQuantity(item.getProductId(), -fulfillQty);

        // ③ Insert ลง StockTransaction (OUT)
        StockTransaction transaction = new StockTransaction();
        transaction.setType("OUT");
        transaction.setProductId(item.getProductId());
        transaction.setQuantity(fulfillQty);
        transaction.setStaffId(warehouseStaffId);
        transaction.setReference("Fulfill Request ID " + item.getRequestId());
        stockTransactionRepository.save(transaction);

        // 14. & 16. Post-Fulfillment Actions
        checkAndUpdateRequestAndOrderStatus(item.getRequestId(), item.getProductId(), fulfillQty);
    }

    // เพิ่ม: Method ช่วยสำหรับตรวจสอบและอัปเดตสถานะ
    private void checkAndUpdateRequestAndOrderStatus(int requestId, int productId, int fulfillQty) {
        if (requestRepository.areAllItemsFulfilled(requestId)) {
            requestRepository.updateRequestStatus(requestId, "Closed");
        }

        Request request = requestRepository.findById(requestId);
        if (request != null && request.getOrderId() != null) {
            orderRepository.updateOrderItemFulfillment(request.getOrderId(), productId, fulfillQty);

        }
    }

}