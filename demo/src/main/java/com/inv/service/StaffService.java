package com.inv.service;

import com.inv.model.Staff;
import com.inv.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StaffService {

    @Autowired
    private UserRepository userRepository; // ควรเปลี่ยนชื่อเป็น StaffRepository ในอนาคต

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Staff createStaff(Staff staff) {
        // 4. Validation: ตรวจสอบว่าอีเมลซ้ำหรือไม่
        if (userRepository.findByEmail(staff.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "อีเมลนี้ถูกใช้แล้ว (Email is already in use)");
        }

        // ตั้งรหัสผ่านเริ่มต้น (ตาม Use Case ข้อ 7)
        // ในระบบจริง อาจจะสร้างรหัสผ่านแบบสุ่ม หรือตั้งเป็นค่า default
        String initialPassword = "defaultPassword123"; // << สามารถเปลี่ยนได้
        staff.setPassword(passwordEncoder.encode(initialPassword));

        // 6. Insert ข้อมูล
        staff.setActive(true); // พนักงานใหม่ควร Active เสมอ
        userRepository.save(staff);

        // 8. คืนข้อมูล Staff ที่สร้างใหม่ (ไม่มีรหัสผ่าน)
        staff.setPassword(null); // ไม่ควรส่ง password hash กลับไป
        return staff;
    }
    public List<Staff> getAllStaff() {
        return userRepository.findAll();
    }

    // อาจเพิ่มฟังก์ชันอื่นๆ สำหรับ Admin ในอนาคต
    // public List<Staff> getAllStaff() { ... }
    // public void deactivateStaff(int staffId) { ... }
}