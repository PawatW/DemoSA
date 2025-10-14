package com.inv.service;

import com.inv.model.Product;
import com.inv.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(int id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product) {
        // 6. Validation
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "กรุณาระบุชื่อสินค้า (Product name is required)");
        }
        if (product.getPricePerUnit() == null || product.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ราคาสินค้าต้องมากกว่า 0 (Price must be greater than 0)");
        }
        if (product.getSupplierId() == null || product.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "กรุณาเลือก Supplier และ Category");
        }

        // 8. & 9. Insert ข้อมูลและคืนผลลัพธ์
        return productRepository.save(product);
    }

    public void adjustQuantity(int productId, int diff) {
        productRepository.updateQuantity(productId, diff);
    }
}
