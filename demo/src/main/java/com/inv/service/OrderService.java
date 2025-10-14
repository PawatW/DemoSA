package com.inv.service;

import com.inv.model.Order;
import com.inv.model.OrderItem;
import com.inv.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public int createOrder(Order order, List<OrderItem> items, int staffId) {
        order.setStaffId(staffId);

        int orderId = orderRepository.save(order);
        for (OrderItem item : items) {
            item.setOrderId(orderId);
            orderRepository.saveOrderItem(item);
        }
        return orderId;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getConfirmedOrders() {
        return orderRepository.findConfirmedOrders();
    }

    public List<OrderItem> getItemsByOrderId(int orderId) {
        return orderRepository.findItemsByOrderId(orderId);
    }

    // เพิ่ม: Service สำหรับดึง Order ที่พร้อมปิด
    public List<Order> getOrdersReadyToClose() {
        return orderRepository.findOrdersReadyToClose();
    }

    // เพิ่ม: Service สำหรับปิด Order
    @Transactional
    public void closeOrder(int orderId, int staffId) {
        // ตรวจสอบว่ามี Request ค้างอยู่หรือไม่
        if (orderRepository.hasPendingRequests(orderId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ยังมีคำขอเบิกสินค้าที่ยังค้างอยู่ ไม่สามารถปิด Order ได้");
        }
        orderRepository.closeOrder(orderId, staffId);
    }
}
