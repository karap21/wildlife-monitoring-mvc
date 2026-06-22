package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PeringatanRepository {

    private final JdbcTemplate jdbcTemplate;

    // FUNGSI INTI: Mesin Pembangkit Peringatan Cerdas (Smart Alert Engine)
    public void generateSmartAlerts() {

        // 1. ALERT BATERAI KRITIS (Baterai <= 15%)
        String sqlBaterai = "INSERT INTO system_alerts (animal_id, alert_type, message, is_read, created_at) " +
                "SELECT animal_id, 'CRITICAL_BATTERY', CONCAT('Kritis: Baterai GPS sisa ', ROUND(battery_level, 1), '%. Segera lakukan penanganan.'), 0, NOW() " +
                "FROM tracking_devices WHERE battery_level <= 15 AND animal_id IS NOT NULL " +
                // Mencegah spam: Jangan buat alert jika hari ini sudah dibuat alert baterai untuk satwa yang sama
                "AND NOT EXISTS (SELECT 1 FROM system_alerts sa WHERE sa.animal_id = tracking_devices.animal_id AND sa.alert_type = 'CRITICAL_BATTERY' AND DATE(sa.created_at) = CURDATE())";
        jdbcTemplate.update(sqlBaterai);

        // 2. ALERT PERANGKAT OFFLINE (Tidak ada sinyal > 24 Jam)
        String sqlOffline = "INSERT INTO system_alerts (animal_id, alert_type, message, is_read, created_at) " +
                "SELECT animal_id, 'OFFLINE_DEVICE', 'Peringatan: Perangkat kehilangan sinyal ke satelit lebih dari 24 jam. Lokasi satwa tidak diketahui.', 0, NOW() " +
                "FROM tracking_devices WHERE last_seen < (NOW() - INTERVAL 1 DAY) AND animal_id IS NOT NULL " +
                "AND NOT EXISTS (SELECT 1 FROM system_alerts sa WHERE sa.animal_id = tracking_devices.animal_id AND sa.alert_type = 'OFFLINE_DEVICE' AND DATE(sa.created_at) = CURDATE())";
        jdbcTemplate.update(sqlOffline);

        // 3. ALERT ANOMALI PERGERAKAN (Tidak terpantau pergerakan > 3 Hari)
        String sqlNoMovement = "INSERT INTO system_alerts (animal_id, alert_type, message, is_read, created_at) " +
                "SELECT animal_id, 'NO_MOVEMENT', 'Anomali: Tidak ada data pergerakan selama 3 hari terakhir. Harap segera instruksikan patroli lapangan!', 0, NOW() " +
                "FROM tracking_devices WHERE last_seen < (NOW() - INTERVAL 3 DAY) AND animal_id IS NOT NULL " +
                "AND NOT EXISTS (SELECT 1 FROM system_alerts sa WHERE sa.animal_id = tracking_devices.animal_id AND sa.alert_type = 'NO_MOVEMENT' AND DATE(sa.created_at) = CURDATE())";
        jdbcTemplate.update(sqlNoMovement);
    }

    public List<Map<String, Object>> ambilSemuaPeringatan() {
        String sql = "SELECT sa.*, a.name AS animal_name " +
                "FROM system_alerts sa " +
                "LEFT JOIN animals a ON sa.animal_id = a.animal_id " +
                "ORDER BY sa.is_read ASC, sa.created_at DESC";
        return jdbcTemplate.queryForList(sql);
    }

    public void tandaiSudahDibaca(Long alertId) {
        jdbcTemplate.update("UPDATE system_alerts SET is_read = 1 WHERE alert_id = ?", alertId);
    }
}