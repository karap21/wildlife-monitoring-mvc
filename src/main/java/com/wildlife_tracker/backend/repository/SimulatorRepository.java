package com.wildlife_tracker.backend.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SimulatorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void tembakSinyalGps(Long deviceId, Double latitude, Double longitude) {
        jdbcTemplate.update("INSERT INTO gps_readings (device_id, latitude, longitude, altitude_meters, accuracy_meters, recorded_at, created_at, updated_at) VALUES (?, ?, ?, 15, 5.0, NOW(), NOW(), NOW())", deviceId, latitude, longitude);
        jdbcTemplate.update("UPDATE tracking_devices SET last_seen = NOW(), battery_level = GREATEST(0, battery_level - 0.05) WHERE device_id = ?", deviceId);

        Long animalId = jdbcTemplate.queryForObject("SELECT animal_id FROM tracking_devices WHERE device_id = ?", Long.class, deviceId);

        if (animalId != null) {
            String sqlZona = "SELECT g.name, g.polygon_coordinates FROM geozones g JOIN animal_geozones ag ON g.zone_id = ag.zone_id WHERE ag.animal_id = ?";
            List<Map<String, Object>> daftarZona = jdbcTemplate.queryForList(sqlZona, animalId);

            boolean isAman = true;

            if (!daftarZona.isEmpty()) {
                isAman = false;
                for (Map<String, Object> zona : daftarZona) {
                    String namaZona = (String) zona.get("name");
                    String polyJson = (String) zona.get("polygon_coordinates");

                    try {
                        List<List<Double>> polygon = objectMapper.readValue(polyJson, new TypeReference<List<List<Double>>>() {});
                        if (apakahDiDalamPoligon(latitude, longitude, polygon)) {
                            isAman = true;
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Gagal membaca zona: " + namaZona);
                    }
                }

                if (!isAman) {
                    String pesan = "PERINGATAN GEOFENCE! Satwa terdeteksi keluar dari zona aman.";
                    jdbcTemplate.update("INSERT INTO system_alerts (animal_id, alert_type, message, is_read, created_at) VALUES (?, 'GEOFENCE_BREACH', ?, FALSE, NOW())", animalId, pesan);
                }
            }
        }
    }

    private boolean apakahDiDalamPoligon(double lat, double lng, List<List<Double>> polygon) {
        boolean result = false;
        int i, j = polygon.size() - 1;
        for (i = 0; i < polygon.size(); i++) {
            double polyLatI = polygon.get(i).get(0), polyLngI = polygon.get(i).get(1);
            double polyLatJ = polygon.get(j).get(0), polyLngJ = polygon.get(j).get(1);

            if (((polyLngI > lng) != (polyLngJ > lng)) &&
                    (lat < (polyLatJ - polyLatI) * (lng - polyLngI) / (polyLngJ - polyLngI) + polyLatI)) {
                result = !result;
            }
            j = i;
        }
        return result;
    }

    // FUNGSI BARU 1: Menarik rekam jejak yang sudah ada agar simulator bisa "Resume"
    public List<Map<String, Object>> ambilRiwayatEksisting() {
        String sql = "SELECT g.latitude, g.longitude, a.animal_id, a.name " +
                "FROM gps_readings g " +
                "JOIN tracking_devices t ON g.device_id = t.device_id " +
                "JOIN animals a ON t.animal_id = a.animal_id " +
                "ORDER BY g.recorded_at ASC";
        return jdbcTemplate.queryForList(sql);
    }

    // FUNGSI BARU 2: Menghapus semua riwayat (Reset Simulator)
    public void resetSistemSimulasi() {
        jdbcTemplate.update("DELETE FROM gps_readings"); // Hapus rute
        jdbcTemplate.update("DELETE FROM system_alerts WHERE alert_type = 'GEOFENCE_BREACH'"); // Hapus notif
        jdbcTemplate.update("UPDATE tracking_devices SET battery_level = 100, last_seen = NULL"); // Isi full baterai
    }
}