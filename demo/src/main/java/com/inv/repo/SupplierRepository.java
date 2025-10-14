package com.inv.repo;

import com.inv.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SupplierRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Supplier mapRow(ResultSet rs, int rowNum) throws SQLException {
        Supplier s = new Supplier();
        s.setSupplierId(rs.getInt("supplier_id"));
        s.setSupplierName(rs.getString("supplier_name"));
        s.setAddress(rs.getString("address"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        return s;
    }

    public List<Supplier> findAll() {
        return jdbcTemplate.query("SELECT * FROM Supplier", this::mapRow);
    }

    public Supplier findById(int id) {
        List<Supplier> list = jdbcTemplate.query(
                "SELECT * FROM Supplier WHERE supplier_id = ?",
                this::mapRow,
                id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public Supplier findByEmail(String email) {
        List<Supplier> list = jdbcTemplate.query("SELECT * FROM Supplier WHERE email = ?", this::mapRow, email);
        return list.isEmpty() ? null : list.get(0);
    }

    // แก้ไข: ให้ method save คืนค่าเป็น Supplier ที่สร้างเสร็จแล้ว
    public Supplier save(Supplier s) {
        String sql = "INSERT INTO Supplier(supplier_name, address, phone, email) VALUES (?,?,?,?) RETURNING *";
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                s.getSupplierName(), s.getAddress(), s.getPhone(), s.getEmail()
        );
    }

}