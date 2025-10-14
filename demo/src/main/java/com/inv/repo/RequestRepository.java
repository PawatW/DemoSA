package com.inv.repo;

import com.inv.model.Request;
import com.inv.model.RequestItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RequestRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ... (ส่วนของ mapRow และ mapRowItem เหมือนเดิม) ...
    private Request mapRow(ResultSet rs, int rowNum) throws SQLException {
        Request r = new Request();
        r.setRequestId(rs.getInt("request_id"));
        r.setRequestDate(rs.getDate("request_date").toLocalDate());
        r.setStatus(rs.getString("status"));
        r.setOrderId((Integer) rs.getObject("order_id"));
        r.setStaffId(rs.getInt("staff_id"));
        r.setDescription(rs.getString("description"));
        r.setApprovedBy((Integer) rs.getObject("approved_by"));
        r.setApprovedDate(rs.getTimestamp("approved_date") != null
                ? rs.getTimestamp("approved_date").toLocalDateTime()
                : null);
        return r;
    }

    private RequestItem mapRowItem(ResultSet rs, int rowNum) throws SQLException {
        RequestItem i = new RequestItem();
        i.setRequestItemId(rs.getInt("request_item_id"));
        i.setRequestId(rs.getInt("request_id"));
        i.setProductId(rs.getInt("product_id"));
        i.setQuantity(rs.getInt("quantity"));
        i.setFulfilledQty(rs.getInt("fulfilled_qty"));
        i.setRemainingQty(rs.getInt("remaining_qty"));
        return i;
    }

    public int save(Request r) {
        // แก้ไข: เพิ่ม staff_id และ status ให้ถูกต้องตาม use case
        return jdbcTemplate.queryForObject(
                "INSERT INTO request(request_date, status, order_id, staff_id, description) " +
                        "VALUES (CURRENT_DATE, ?, ?, ?, ?) RETURNING request_id",
                Integer.class,
                "Awaiting Approval", // status เริ่มต้น
                r.getOrderId(),
                r.getStaffId(),
                r.getDescription()
        );
    }

    public void saveRequestItem(RequestItem i) {
        // ไม่มีการแก้ไข
        jdbcTemplate.update(
                "INSERT INTO requestitem(request_id, product_id, quantity, fulfilled_qty, remaining_qty) " +
                        "VALUES (?,?,?,?,?)",
                i.getRequestId(), i.getProductId(), i.getQuantity(),
                i.getFulfilledQty(), i.getRemainingQty()
        );
    }

    public List<Request> findPendingRequests() {
        // โค้ดเดิมถูกต้องแล้ว (ครอบคลุมทั้ง Pending และ Awaiting Approval)
        return jdbcTemplate.query(
                "SELECT * FROM request WHERE status = 'Pending' OR status = 'Awaiting Approval'",
                this::mapRow
        );
    }

    // เพิ่ม: ค้นหา RequestItem รายชิ้น
    public RequestItem findItemById(int requestItemId) {
        String sql = "SELECT * FROM requestitem WHERE request_item_id = ?";
        List<RequestItem> items = jdbcTemplate.query(sql, this::mapRowItem, requestItemId);
        return items.isEmpty() ? null : items.get(0);
    }

    // เพิ่ม: ค้นหา Request ด้วย ID
    public Request findById(int requestId) {
        String sql = "SELECT * FROM request WHERE request_id = ?";
        List<Request> requests = jdbcTemplate.query(sql, this::mapRow, requestId);
        return requests.isEmpty() ? null : requests.get(0);
    }

    // เพิ่ม: ค้นหา Request ที่ได้รับการอนุมัติแล้ว
    public List<Request> findApprovedRequests() {
        return jdbcTemplate.query(
                "SELECT * FROM Request WHERE status = 'Approved'",
                this::mapRow
        );
    }

    // เพิ่ม: อัปเดตจำนวนที่เบิกไปแล้วใน RequestItem
    public void updateItemFulfillment(int requestItemId, int fulfillQty) {
        jdbcTemplate.update(
                "UPDATE requestitem SET fulfilled_qty = fulfilled_qty + ? WHERE request_item_id = ?",
                fulfillQty, requestItemId
        );
    }

    // เพิ่ม: ตรวจสอบว่า RequestItem ทั้งหมดถูกเบิกครบแล้วหรือยัง
    public boolean areAllItemsFulfilled(int requestId) {
        String sql = "SELECT COUNT(*) FROM requestitem WHERE request_id = ? AND remaining_qty > 0";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, requestId);
        return count != null && count == 0;
    }

    // เพิ่ม: อัปเดตสถานะของ Request
    public void updateRequestStatus(int requestId, String status) {
        jdbcTemplate.update("UPDATE request SET status = ? WHERE request_id = ?", status, requestId);
    }
    // This method finds all items associated with a specific request ID.
    public List<RequestItem> findItemsByRequestId(int requestId) {
        return jdbcTemplate.query(
                "SELECT * FROM request_item WHERE request_id = ?",
                this::mapRowItem,
                requestId
        );
    }
    // This method updates the status of a request, for example, to 'Approved' or 'Rejected'.
    public void updateStatus(int requestId, String status, Integer approverId) {
        jdbcTemplate.update(
                "UPDATE request SET status = ?, approved_by = ?, approved_date = NOW() WHERE request_id = ?",
                status, approverId, requestId
        );
    }

    // เพิ่ม: Method ค้นหา Request ที่เบิกของครบแล้วและพร้อมที่จะปิด
    public List<Request> findReadyToCloseRequests() {
        String sql = "SELECT * FROM Request r " +
                "WHERE r.status = 'Approved' " +
                "AND NOT EXISTS (SELECT 1 FROM RequestItem ri WHERE ri.request_id = r.request_id AND ri.remaining_qty > 0)";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    // เพิ่ม: Method สำหรับการปิด Request ด้วยตนเอง
    public void closeRequest(int requestId, int staffId) {
        jdbcTemplate.update(
                "UPDATE Request SET status = 'Closed', closed_by = ?, closed_date = NOW() WHERE request_id = ?",
                staffId, requestId
        );
    }
}