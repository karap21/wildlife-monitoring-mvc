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
        // 1. Simpan ke Riwayat
        jdbcTemplate.update("INSERT INTO gps_readings (device_id, latitude, longitude, altitude_meters, accuracy_meters, recorded_at, created_at, updated_at) VALUES (?, ?, ?, 15, 5.0, NOW(), NOW(), NOW())", deviceId, latitude, longitude);
        jdbcTemplate.update("UPDATE tracking_devices SET last_seen = NOW() WHERE device_id = ?", deviceId);

        // 2. Cari Animal ID dari Device ini
        Long animalId = jdbcTemplate.queryForObject("SELECT animal_id FROM tracking_devices WHERE device_id = ?", Long.class, deviceId);

        if (animalId != null) {
            // 3. Tarik semua Zona Aman (Geozone) milik Hewan ini
            String sqlZona = "SELECT g.name, g.polygon_coordinates FROM geozones g JOIN animal_geozones ag ON g.zone_id = ag.zone_id WHERE ag.animal_id = ?";
            List<Map<String, Object>> daftarZona = jdbcTemplate.queryForList(sqlZona, animalId);

            boolean isAman = daftarZona.isEmpty(); // Jika tidak punya zona, anggap aman

            for (Map<String, Object> zona : daftarZona) {
                String polygonJson = (String) zona.get("polygon_coordinates");
                String namaZona = (String) zona.get("name");

                try {
                    // Konversi String JSON menjadi Array List of List [lat, lng]
                    List<List<Double>> polygon = objectMapper.readValue(polygonJson, new TypeReference<List<List<Double>>>(){});

                    // ALGORITMA RAY-CASTING: Mengecek apakah titik (lat, lng) ada di dalam Poligon
                    if (apakahDiDalamPoligon(latitude, longitude, polygon)) {
                        isAman = true;
                        break; // Jika ada di dalam setidaknya 1 zona miliknya, berarti aman
                    }
                } catch (Exception e) {
                    System.out.println("Gagal membaca koordinat poligon zona: " + namaZona);
                }
            }

            // 4. Jika di luar zona, ciptakan Notifikasi Pelanggaran (Alert)
            if (!isAman) {
                String pesan = "PERINGATAN GEOFENCE! Satwa terdeteksi keluar dari zona aman pelindungnya.";
                jdbcTemplate.update("INSERT INTO system_alerts (animal_id, alert_type, message, is_read, created_at) VALUES (?, 'GEOFENCE_BREACH', ?, FALSE, NOW())", animalId, pesan);
            }
        }
    }

    // Fungsi Matematika Spasial (Ray-Casting Algorithm)
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
}