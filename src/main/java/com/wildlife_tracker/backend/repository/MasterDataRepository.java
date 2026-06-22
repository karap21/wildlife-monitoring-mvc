package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MasterDataRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> ambilSemuaStatusKonservasi() {
        return jdbcTemplate.queryForList("SELECT * FROM conservation_statuses ORDER BY display_name ASC");
    }

    public List<Map<String, Object>> ambilSemuaSpesies(String keyword) {
        String sql = "SELECT s.*, c.display_name AS status_name, c.color_code " +
                "FROM species s LEFT JOIN conservation_statuses c ON s.conservation_status_id = c.id ";

        if (keyword != null && !keyword.isEmpty()) {
            sql += "WHERE LOWER(s.common_name) LIKE ? OR LOWER(s.scientific_name) LIKE ? ";
            String queryKeyword = "%" + keyword.toLowerCase() + "%";
            return jdbcTemplate.queryForList(sql + "ORDER BY s.common_name ASC", queryKeyword, queryKeyword);
        }
        return jdbcTemplate.queryForList(sql + "ORDER BY s.common_name ASC");
    }

    public void tambahSpesies(String commonName, String scientificName, Long statusId, String description) {
        String sql = "INSERT INTO species (common_name, scientific_name, conservation_status_id, description, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, commonName, scientificName, statusId, description);
    }

    // FUNGSI BARU: Simpan dan kembalikan ID-nya (Untuk fitur modal Hewan)
    public Long tambahSpesiesGetId(String commonName, String scientificName, Long statusId) {
        String sql = "INSERT INTO species (common_name, scientific_name, conservation_status_id, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, commonName);
            ps.setString(2, scientificName);
            if (statusId != null) {
                ps.setLong(3, statusId);
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void hapusSpesies(Long id) {
        jdbcTemplate.update("DELETE FROM species WHERE species_id = ?", id);
    }

    // =========================================================================
    // KUMPULAN FUNGSI UNTUK KEBUTUHAN LAPORAN (Sudah dimasukkan ke dalam kelas)
    // =========================================================================

    // 1. Mengambil jumlah hewan per spesies
    public List<Map<String, Object>> getStatistikSpesiesLaporan() {
        String sql = "SELECT s.common_name, COUNT(a.animal_id) as total_hewan " +
                "FROM species s LEFT JOIN animals a ON s.species_id = a.species_id " +
                "GROUP BY s.species_id HAVING total_hewan > 0 " +
                "ORDER BY total_hewan DESC";
        return jdbcTemplate.queryForList(sql);
    }

    // 2. Mengambil detail baterai dan jumlah pelanggaran (Geofence Breach) per hewan
    public List<Map<String, Object>> getDetailKondisiSatwaLaporan() {
        String sql = "SELECT a.name AS animal_name, s.common_name AS species_name, " +
                "COALESCE(t.battery_level, 0) AS battery_level, " +
                "(SELECT COUNT(*) FROM system_alerts sa WHERE sa.animal_id = a.animal_id AND sa.alert_type = 'GEOFENCE_BREACH') AS total_pelanggaran " +
                "FROM animals a " +
                "LEFT JOIN species s ON a.species_id = s.species_id " +
                "LEFT JOIN tracking_devices t ON a.animal_id = t.animal_id " +
                "WHERE a.is_active = 1 " +
                "ORDER BY s.common_name ASC, a.name ASC";
        return jdbcTemplate.queryForList(sql);
    }
}