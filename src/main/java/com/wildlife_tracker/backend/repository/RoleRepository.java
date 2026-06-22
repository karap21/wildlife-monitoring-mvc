package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilSemuaRole() {
        return jdbcTemplate.queryForList("SELECT * FROM roles ORDER BY id ASC");
    }

    public void tambahRole(String namaRole) {
        jdbcTemplate.update("INSERT INTO roles (nama_role) VALUES (UPPER(?))", namaRole);
    }

    public void hapusRole(Long id) {
        jdbcTemplate.update("DELETE FROM roles WHERE id = ?", id);
    }

    // PENYESUAIAN PENTING: Kueri ini telah disesuaikan dengan nama tabel 'roles' dan kolom 'nama_role' Anda
    public List<String> ambilHakAksesOlehRole(String roleName) {
        String sql = "SELECT p.name " +
                "FROM permission p " +
                "JOIN role_permission rp ON p.id = rp.permission_id " +
                "JOIN roles r ON r.id = rp.role_id " +
                "WHERE r.nama_role = ?";

        try {
            return jdbcTemplate.queryForList(sql, String.class, roleName);
        } catch (Exception e) {
            return List.of();
        }
    }
}