package com.wildlife_tracker.backend.repository;

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

    // FUNGSI LOGIN YANG SUDAH DIPERBAIKI
    // Langsung membaca kolom 'role' dari tabel 'akun' Anda
    public Map<String, Object> validasiLogin(String email, String password) {
        String sql = "SELECT id, nama_lengkap, email, role FROM akun WHERE email = ? AND password = ?";
        try {
            return jdbcTemplate.queryForMap(sql, email, password);
        } catch (Exception e) {
            return null;
        }
    }

    // Fungsi untuk fitur manajemen admin
    public List<Map<String, Object>> ambilSemuaAkun() {
        String sql = "SELECT id, username, nama_lengkap, email, role, institusi FROM akun ORDER BY id DESC";
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