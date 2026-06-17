package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PerangkatRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilSemuaPerangkat() {
        String sql = "SELECT t.*, a.name AS animal_name " +
                "FROM tracking_devices t " +
                "LEFT JOIN animals a ON t.animal_id = a.animal_id " +
                "ORDER BY t.created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public void tambahPerangkat(Long animalId, String model, String serialNumber, Integer batteryLife, String installDate) {
        String sql = "INSERT INTO tracking_devices (animal_id, model, serial_number, battery_life_months, installation_date, battery_level, signal_strength, status, last_seen, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, 100.0, 100.0, 'ONLINE', NOW(), NOW(), NOW())";
        Object finalDate = (installDate == null || installDate.isEmpty()) ? null : installDate;
        jdbcTemplate.update(sql, animalId, model, serialNumber, batteryLife, finalDate);
    }

    public void updatePerangkat(Long deviceId, Long animalId, String model, String serialNumber, Integer batteryLife, String installDate) {
        String sql = "UPDATE tracking_devices SET animal_id = ?, model = ?, serial_number = ?, battery_life_months = ?, installation_date = ?, updated_at = NOW() WHERE device_id = ?";
        Object finalDate = (installDate == null || installDate.isEmpty()) ? null : installDate;
        jdbcTemplate.update(sql, animalId, model, serialNumber, batteryLife, finalDate, deviceId);
    }

    public void hapusPerangkat(Long deviceId) {
        jdbcTemplate.update("DELETE FROM tracking_devices WHERE device_id = ?", deviceId);
    }
}