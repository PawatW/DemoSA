package com.inv.service;

import com.inv.model.OrderItem;
import com.inv.model.Request;
import com.inv.model.RequestItem;
import com.inv.repo.OrderRepository;
import com.inv.repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private OrderRepository orderRepository;

    public int createRequest(Request req, List<RequestItem> items) {
        int requestId = requestRepository.save(req);
        for (RequestItem i : items) {
            i.setRequestId(requestId);
            requestRepository.saveRequestItem(i);
        }
        return requestId;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public List<Request> getPendingRequests() {
        return requestRepository.findPendingRequests();
    }

    public List<RequestItem> getItemsByRequestId(int requestId) {
        return requestRepository.findItemsByRequestId(requestId);
    }

    public void approveRequest(int requestId, int approverId) {
        requestRepository.updateStatus(requestId, "Approved", approverId);
    }

    public void rejectRequest(int requestId, int approverId) {
        requestRepository.updateStatus(requestId, "Rejected", approverId);
    }

    public List<OrderItem> getOrderItems(int orderId) {
        return orderRepository.findItemsByOrderId(orderId);
    }

    // เพิ่ม: Service method สำหรับดึง Request ที่พร้อมปิด
    public List<Request> getReadyToCloseRequests() {
        return requestRepository.findReadyToCloseRequests();
    }

    // เพิ่ม: Service method สำหรับการปิด Request
    public void closeRequest(int requestId, int staffId) {
        // อาจเพิ่ม Validation เพิ่มเติมได้ที่นี่ เช่น ตรวจสอบว่า Request อยู่ในสถานะ 'Approved' จริงๆ
        requestRepository.closeRequest(requestId, staffId);
    }
}
