package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StatistikRepository {

    private final JdbcTemplate jdbcTemplate;

    // --- FUNGSI LAMA (TETAP DIPERTAHANKAN) ---
    public long count(String table) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Long.class);
    }

    public long countActiveAnimals() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM animals WHERE is_active = TRUE", Long.class);
    }

    public List<Map<String, Object>> ambilDataPopulasiSpesies() {
        String sql = "SELECT s.common_name AS label, COUNT(a.animal_id) AS value " +
                "FROM species s LEFT JOIN animals a ON s.species_id = a.species_id " +
                "GROUP BY s.species_id, s.common_name";
        return jdbcTemplate.queryForList(sql);
    }

    // --- FUNGSI BARU: MENGHITUNG PERINGATAN ---
    public long countUnreadAlerts() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM system_alerts WHERE is_read = 0", Long.class);
        } catch (Exception e) {
            return 0L;
        }
    }
}