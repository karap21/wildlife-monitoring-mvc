package com.wildlife_tracker.backend.repository;

import com.wildlife_tracker.backend.model.AkunPegawai;
import com.wildlife_tracker.backend.model.Administrator;
import com.wildlife_tracker.backend.model.Peneliti;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AkunRepository {

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> cariBerdasarkanEmail(String email) {
        String sql = "SELECT * FROM akun WHERE email = ?";
        try {
            return jdbcTemplate.queryForMap(sql, email);
        } catch (Exception e) {
            return null;
        }
    }

    public AkunPegawai validasiLogin(String email, String password) {
        String sql = "SELECT id, nama_lengkap, email, role FROM akun WHERE email = ? AND password = ?";
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(sql, email, password);
            Long id = ((Number) row.get("id")).longValue();
            String nama = (String) row.get("nama_lengkap");
            String mail = (String) row.get("email");
            String role = (String) row.get("role");

            if ("ADMINISTRATOR".equalsIgnoreCase(role)) {
                return new Administrator(id, nama, mail);
            } else if ("PENELITI".equalsIgnoreCase(role)) {
                return new Peneliti(id, nama, mail);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // FUNGSI INI YANG DIPERBAIKI (Penyesuaian dengan Database baru)
    public List<Map<String, Object>> ambilSemuaAkun() {
        String sql = "SELECT id, nama_lengkap, email, role AS nama_role FROM akun ORDER BY id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public void tambahAkun(String namaLengkap, String email, String password, String role) {
        jdbcTemplate.update("INSERT INTO akun (nama_lengkap, email, password, role) VALUES (?, ?, ?, ?)",
                namaLengkap, email, password, role);
    }

    public void hapusAkun(Long id) {
        jdbcTemplate.update("DELETE FROM akun WHERE id = ?", id);
    }
}