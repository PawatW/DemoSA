package com.inv.service;

import com.inv.model.Order;
import com.inv.model.OrderItem;
import com.inv.repo.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Order> getConfirmedOrders() {
        return orderRepository.findConfirmedOrders();
    }

    public List<OrderItem> getItemsByOrderId(int orderId) {
        return orderRepository.findItemsByOrderId(orderId);
    }
}
