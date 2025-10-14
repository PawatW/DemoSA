package com.inv.service;

import com.inv.model.Staff;
import com.inv.repo.UserRepository;
import com.inv.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String password) {
        // 4. & 5. ค้นหาผู้ใช้
        Staff staff = userRepository.findByEmail(email);
        if (staff == null || !passwordEncoder.matches(password, staff.getPassword())) {
            // 7. ตรวจรหัสผ่าน
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // 6. เพิ่ม: ตรวจสอบสถานะ Active
        if (!staff.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "บัญชีผู้ใช้นี้ถูกระงับ (Account is deactivated)");
        }

        // 8. สร้าง Token
        return jwtUtil.generateToken(String.valueOf(staff.getStaffId()), staff.getRole());
    }



//    public void register(Staff staff) {
//        System.out.println("AuthService - Registering: " + staff.getEmail());
//        staff.setActive(true);  // ← Add this
//        staff.setPassword(passwordEncoder.encode(staff.getPassword()));
//        userRepository.save(staff);
//        System.out.println("AuthService - Registration successful");
//    }
}

