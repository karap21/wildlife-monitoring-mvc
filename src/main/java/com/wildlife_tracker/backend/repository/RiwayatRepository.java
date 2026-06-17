package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RiwayatRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilRiwayatHewan(Long animalId, String startDate, String endDate) {
        String sql = "SELECT g.latitude, g.longitude, g.recorded_at, g.altitude_meters " +
                "FROM gps_readings g " +
                "JOIN tracking_devices t ON g.device_id = t.device_id " +
                "WHERE t.animal_id = ? ";

        List<Object> params = new ArrayList<>();
        params.add(animalId);

        // Filter rentang waktu jika diisi
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            sql += "AND DATE(g.recorded_at) BETWEEN ? AND ? ";
            params.add(startDate);
            params.add(endDate);
        }

        sql += "ORDER BY g.recorded_at ASC LIMIT 500"; // Batasi 500 titik agar browser tidak lag

        return jdbcTemplate.queryForList(sql, params.toArray());
    }
}