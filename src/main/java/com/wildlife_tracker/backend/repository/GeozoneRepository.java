package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class GeozoneRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilSemuaZona() {
        String sql = "SELECT g.zone_id, g.name, g.polygon_coordinates, " +
                "GROUP_CONCAT(a.name SEPARATOR ', ') AS animal_names, " +
                "GROUP_CONCAT(ag.animal_id SEPARATOR ',') AS animal_ids " +
                "FROM geozones g " +
                "LEFT JOIN animal_geozones ag ON g.zone_id = ag.zone_id " +
                "LEFT JOIN animals a ON ag.animal_id = a.animal_id " +
                "GROUP BY g.zone_id ORDER BY g.created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public void simpanZonaBaru(String name, String polygon, List<Long> animalIds) {
        String sql = "INSERT INTO geozones (name, polygon_coordinates, created_at) VALUES (?, ?, NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, polygon);
            return ps;
        }, keyHolder);

        Long zoneId = keyHolder.getKey().longValue();
        simpanRelasiHewan(zoneId, animalIds);
    }

    // FUNGSI BARU: Untuk Mengedit Zona Eksisting
    public void updateZona(Long zoneId, String name, String polygon, List<Long> animalIds) {
        jdbcTemplate.update("UPDATE geozones SET name = ?, polygon_coordinates = ? WHERE zone_id = ?", name, polygon, zoneId);
        // Hapus relasi satwa lama, lalu masukkan yang baru (Timpa)
        jdbcTemplate.update("DELETE FROM animal_geozones WHERE zone_id = ?", zoneId);
        simpanRelasiHewan(zoneId, animalIds);
    }

    private void simpanRelasiHewan(Long zoneId, List<Long> animalIds) {
        if (animalIds != null && !animalIds.isEmpty()) {
            for (Long aId : animalIds) {
                jdbcTemplate.update("INSERT INTO animal_geozones (animal_id, zone_id) VALUES (?, ?)", aId, zoneId);
            }
        }
    }

    public void hapusZona(Long zoneId) {
        jdbcTemplate.update("DELETE FROM geozones WHERE zone_id = ?", zoneId);
    }
}