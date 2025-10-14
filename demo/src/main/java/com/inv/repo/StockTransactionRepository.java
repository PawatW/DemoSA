package com.inv.repo;

import com.inv.model.StockTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StockTransactionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(StockTransaction transaction) {
        // ใน schema.sql ไม่มีคอลัมน์ note แต่มี reference เลยใช้ reference แทน
        jdbcTemplate.update(
                "INSERT INTO StockTransaction(type, product_id, quantity, staff_id, reference) VALUES (?, ?, ?, ?, ?)",
                transaction.getType(),
                transaction.getProductId(),
                transaction.getQuantity(),
                transaction.getStaffId(),
                transaction.getReference()
        );
    }
    public List<StockTransaction> findAll() {
        return jdbcTemplate.query("SELECT * FROM StockTransaction ORDER BY transaction_date DESC", this::mapRow);
    }
}