package com.inv.controller;

import com.inv.model.Order;
import com.inv.model.OrderItem;
import com.inv.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public int createOrder(@RequestBody OrderRequest orderRequest, Principal principal) {

        String staffIdStr = principal.getName();
        int staffId = Integer.parseInt(staffIdStr);

        return orderService.createOrder(orderRequest.getOrder(), orderRequest.getItems(), staffId);
    }

    @GetMapping("/confirmed")
    public List<Order> getConfirmedOrders() {
        return orderService.getConfirmedOrders();
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable int orderId) {
        return orderService.getItemsByOrderId(orderId);
    }

    public static class OrderRequest {
        private Order order;
        private List<OrderItem> items;
        // getters and setters
        public Order getOrder() { return order; }
        public void setOrder(Order order) { this.order = order; }
        public List<OrderItem> getItems() { return items; }
        public void setItems(List<OrderItem> items) { this.items = items; }
    }
}
