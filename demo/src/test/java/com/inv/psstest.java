package com.inv;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class psstest {
    public static void main(String[] args) {
        // ลองปรับค่า strength (cost factor) ได้ เช่น 10, 12, 14
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);

        String rawPassword = "1234";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Raw password   : " + rawPassword);
        System.out.println("BCrypt encoded : " + encodedPassword);

        // ทดสอบการ match
        boolean matches = encoder.matches("1234", encodedPassword);
        System.out.println("Match result   : " + matches);

        // ทดสอบ password ผิด
        boolean wrong = encoder.matches("654321", encodedPassword);
        System.out.println("Match wrong    : " + wrong);
    }
}
