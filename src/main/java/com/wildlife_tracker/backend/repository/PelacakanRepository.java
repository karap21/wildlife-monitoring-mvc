package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PelacakanRepository {

    private final JdbcTemplate jdbcTemplate;

    // Kueri kompleks untuk mencari lokasi TERAKHIR dari masing-masing satwa
    public List<Map<String, Object>> ambilLokasiTerakhirSatwa() {
        String sql = "SELECT g.latitude, g.longitude, g.recorded_at, " +
                "a.name AS animal_name, s.common_name AS species_name, " +
                "t.serial_number, t.battery_level " +
                "FROM gps_readings g " +
                "INNER JOIN (" +
                "    SELECT device_id, MAX(recorded_at) AS max_time " +
                "    FROM gps_readings " +
                "    GROUP BY device_id" +
                ") latest ON g.device_id = latest.device_id AND g.recorded_at = latest.max_time " +
                "JOIN tracking_devices t ON g.device_id = t.device_id " +
                "JOIN animals a ON t.animal_id = a.animal_id " +
                "JOIN species s ON a.species_id = s.species_id " +
                "WHERE a.is_active = TRUE";

        return jdbcTemplate.queryForList(sql);
    }
}