package com.inv.controller;

import com.inv.model.Staff;
import com.inv.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // 1. Endpoint สำหรับ Admin เพื่อสร้าง Staff
    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff newStaff) {
        Staff createdStaff = staffService.createStaff(newStaff);
        return ResponseEntity.ok(createdStaff);
    }
}