package com.inv.repo;

import com.inv.model.Staff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setStaffName(rs.getString("staff_name"));
        s.setRole(rs.getString("role"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setActive(rs.getBoolean("active"));
        return s;
    }

    public List<Staff> findAll() {
        return jdbcTemplate.query("SELECT * FROM Staff ORDER BY staff_name", this::mapRow);
    }

    public Staff findByEmail(String email) {
        List<Staff> list = jdbcTemplate.query(
                "SELECT * FROM Staff WHERE email = ?",
                this::mapRow,
                email
        );
        return list.isEmpty() ? null : list.get(0);
    }

    public void save(Staff staff) {
        jdbcTemplate.update(
                "INSERT INTO Staff(staff_name, role, phone, email, password, active) VALUES (?,?,?,?,?,?)",
                staff.getStaffName(), staff.getRole(), staff.getPhone(), staff.getEmail(),
                staff.getPassword(), staff.isActive()
        );
    }
}
