package com.inv.service;

import com.inv.model.Supplier;
import com.inv.repo.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(int id) {
        return supplierRepository.findById(id);
    }

    public Supplier createSupplier(Supplier supplier) {
        // 4. Validation
        if (supplier.getSupplierName() == null || supplier.getSupplierName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "กรุณาระบุชื่อ Supplier (Supplier name is required)");
        }

        // ตรวจสอบอีเมลซ้ำ (ถ้ามี)
        if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
            if (supplierRepository.findByEmail(supplier.getEmail()) != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "อีเมลนี้มีในระบบแล้ว (Email already exists)");
            }
        }

        // 6. & 7. Insert ข้อมูลและคืนผลลัพธ์
        return supplierRepository.save(supplier);
    }
}