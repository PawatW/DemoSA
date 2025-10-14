package com.inv.repo;

import com.inv.model.Order;
import com.inv.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class OrderRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ... (ส่วนของ mapRow และ mapRowItem เหมือนเดิม) ...
    private Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setOrderDate(rs.getDate("order_date").toLocalDate());
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setStaffId(rs.getInt("staff_id"));
        return o;
    }

    private OrderItem mapRowItem(ResultSet rs, int rowNum) throws SQLException {
        OrderItem i = new OrderItem();
        i.setOrderItemId(rs.getInt("order_item_id"));
        i.setOrderId(rs.getInt("order_id"));
        i.setProductId(rs.getInt("product_id"));
        i.setQuantity(rs.getInt("quantity"));
        i.setUnitPrice(rs.getBigDecimal("unit_price"));
        i.setLineTotal(rs.getBigDecimal("line_total"));
        i.setFulfilledQty(rs.getInt("fulfilled_qty"));
        i.setRemainingQty(rs.getInt("remaining_qty"));
        return i;
    }


    public int save(Order o) {
        // แก้ไข: เปลี่ยนจาก orders เป็น "Order"
        return jdbcTemplate.queryForObject(
                "INSERT INTO \"Order\"(order_date, total_amount, status, customer_id, staff_id) " +
                        "VALUES (CURRENT_DATE, ?, ?, ?, ?) RETURNING order_id",
                Integer.class,
                o.getTotalAmount(), o.getStatus(), o.getCustomerId(), o.getStaffId()
        );
    }

    public void saveOrderItem(OrderItem i) {
        // แก้ไข: เปลี่ยนจาก order_item เป็น OrderItem
        jdbcTemplate.update(
                "INSERT INTO orderitem(order_id, product_id, quantity, unit_price, line_total, fulfilled_qty, remaining_qty) " +
                        "VALUES (?,?,?,?,?,?,?)",
                i.getOrderId(), i.getProductId(), i.getQuantity(),
                i.getUnitPrice(), i.getLineTotal(), i.getFulfilledQty(), i.getRemainingQty()
        );
    }

    public List<Order> findConfirmedOrders() {
        // แก้ไข: เปลี่ยนจาก orders เป็น "Order"
        return jdbcTemplate.query("SELECT * FROM \"Order\" WHERE status = 'Confirmed'", this::mapRow);
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        // แก้ไข: เปลี่ยนจาก order_item เป็น OrderItem
        return jdbcTemplate.query(
                "SELECT * FROM orderitem WHERE order_id = ?",
                this::mapRowItem,
                orderId
        );
    }

    // เพิ่ม: อัปเดตจำนวนที่เบิกไปแล้วใน OrderItem
    public void updateOrderItemFulfillment(int orderId, int productId, int fulfillQty) {
        jdbcTemplate.update(
                "UPDATE OrderItem SET fulfilled_qty = fulfilled_qty + ? WHERE order_id = ? AND product_id = ?",
                fulfillQty, orderId, productId
        );
    }

    // เพิ่ม: ตรวจสอบว่า OrderItem ทั้งหมดถูกเบิกครบแล้วหรือยัง
    public boolean areAllOrderItemsFulfilled(int orderId) {
        String sql = "SELECT COUNT(*) FROM OrderItem WHERE order_id = ? AND remaining_qty > 0";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, orderId);
        return count != null && count == 0;
    }

    // เพิ่ม: อัปเดตสถานะของ Order
    public void updateOrderStatus(int orderId, String status) {
        jdbcTemplate.update("UPDATE \"Order\" SET status = ? WHERE order_id = ?", status, orderId);
    }
}