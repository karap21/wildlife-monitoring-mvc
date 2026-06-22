package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RiwayatRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilRiwayatHewan(List<Long> animalIds, String startDate, String endDate) {
        String sql = "SELECT g.latitude, g.longitude, g.recorded_at, g.altitude_meters, a.animal_id, a.name AS animal_name " +
                "FROM gps_readings g " +
                "JOIN tracking_devices t ON g.device_id = t.device_id " +
                "JOIN animals a ON t.animal_id = a.animal_id " +
                "WHERE 1=1 ";

        List<Object> params = new ArrayList<>();

        // LOGIKA BARU: Dukungan Multi-Satwa (List)
        if (animalIds != null && !animalIds.isEmpty()) {
            String inSql = animalIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            sql += "AND t.animal_id IN (" + inSql + ") ";
        } else {
            return new ArrayList<>(); // Kembalikan list kosong jika tidak ada yang diceklis
        }

        // Filter rentang waktu jika diisi
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            sql += "AND DATE(g.recorded_at) BETWEEN ? AND ? ";
            params.add(startDate);
            params.add(endDate);
        }

        sql += "ORDER BY g.recorded_at ASC LIMIT 1000"; // Batas ditingkatkan untuk multi-satwa

        return jdbcTemplate.queryForList(sql, params.toArray());
    }
}