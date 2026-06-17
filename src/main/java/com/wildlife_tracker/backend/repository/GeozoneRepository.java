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
                "GROUP_CONCAT(a.name SEPARATOR ', ') AS linked_animals " +
                "FROM geozones g " +
                "LEFT JOIN animal_geozones ag ON g.zone_id = ag.zone_id " +
                "LEFT JOIN animals a ON ag.animal_id = a.animal_id " +
                "GROUP BY g.zone_id, g.name, g.polygon_coordinates " +
                "ORDER BY g.zone_id DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public void simpanZona(String name, String polygonCoordinates, List<Long> animalIds) {
        // 1. Simpan Zona ke tabel geozones dan ambil ID barunya
        String sqlZone = "INSERT INTO geozones (name, polygon_coordinates) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlZone, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, polygonCoordinates);
            return ps;
        }, keyHolder);

        Long newZoneId = keyHolder.getKey().longValue();

        // 2. Simpan daftar hewan ke tabel relasi animal_geozones
        if (animalIds != null && !animalIds.isEmpty()) {
            for (Long animalId : animalIds) {
                jdbcTemplate.update("INSERT INTO animal_geozones (animal_id, zone_id) VALUES (?, ?)", animalId, newZoneId);
            }
        }
    }

    public void hapusZona(Long zoneId) {
        // Karena kita pakai ON DELETE CASCADE di SQL, hapus zona otomatis menghapus relasinya
        jdbcTemplate.update("DELETE FROM geozones WHERE zone_id = ?", zoneId);
    }
}