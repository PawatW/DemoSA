package com.inv.repo;

import com.inv.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setDescription(rs.getString("description"));
        p.setUnit(rs.getString("unit"));
        p.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
        p.setSupplierId((Integer) rs.getObject("supplier_id"));
        p.setCategoryId((Integer) rs.getObject("category_id"));
        p.setQuantity(rs.getInt("quantity"));
        p.setImageUrl(rs.getString("image_url"));
        return p;
    }

    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM product ORDER BY product_name", this::mapRow);
    }

    public Product findById(int id) {
        List<Product> list = jdbcTemplate.query(
                "SELECT * FROM product WHERE product_id = ?",
                this::mapRow,
                id
        );
        return list.isEmpty() ? null : list.get(0);
    }

    // แก้ไข: ให้ method save คืนค่าเป็น Product ที่สร้างเสร็จแล้ว
    public Product save(Product p) {
        String sql = "INSERT INTO product (product_name, description, unit, price_per_unit, supplier_id, category_id, quantity, image_url) " +
                "VALUES (?,?,?,?,?,?,?,?) RETURNING *";
        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                p.getProductName(),
                p.getDescription(),
                p.getUnit(),
                p.getPricePerUnit(),
                p.getSupplierId(),
                p.getCategoryId(),
                0, // Quantity เริ่มต้นเป็น 0 ตาม use case
                p.getImageUrl()
        );
    }

    public void updateQuantity(int productId, int diff) {
        jdbcTemplate.update("UPDATE product SET quantity = quantity + ? WHERE product_id = ?", diff, productId);
    }
}
