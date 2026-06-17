package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MasterDataRepository {

    private final JdbcTemplate jdbcTemplate;

    // --- MANAJEMEN STATUS KONSERVASI ---
    public List<Map<String, Object>> ambilSemuaStatusKonservasi() {
        return jdbcTemplate.queryForList("SELECT * FROM conservation_statuses ORDER BY display_name ASC");
    }

    // --- MANAJEMEN SPESIES ---
    // Menggunakan LEFT JOIN agar displayName dari status konservasi ikut terbaca
    public List<Map<String, Object>> ambilSemuaSpesies(String keyword) {
        String sql = "SELECT s.*, c.display_name AS status_name, c.color_code " +
                "FROM species s LEFT JOIN conservation_statuses c ON s.conservation_status_id = c.id ";

        if (keyword != null && !keyword.isEmpty()) {
            sql += "WHERE LOWER(s.common_name) LIKE ? OR LOWER(s.scientific_name) LIKE ? ";
            String queryKeyword = "%" + keyword.toLowerCase() + "%";
            return jdbcTemplate.queryForList(sql + "ORDER BY s.common_name ASC", queryKeyword, queryKeyword);
        }

        return jdbcTemplate.queryForList(sql + "ORDER BY s.common_name ASC");
    }

    public void tambahSpesies(String commonName, String scientificName, Long statusId, String description) {
        String sql = "INSERT INTO species (common_name, scientific_name, conservation_status_id, description, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, commonName, scientificName, statusId, description);
    }

    public void hapusSpesies(Long id) {
        jdbcTemplate.update("DELETE FROM species WHERE species_id = ?", id);
    }
}