package com.wildlife_tracker.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class HewanRepository {

    private final JdbcTemplate jdbcTemplate;

    // FUNGSI PENYELAMAT (Method Overloading)
    // Controller lama (seperti Riwayat/Geozone) yang hanya kirim 1 parameter akan dialihkan ke sini
    public List<Map<String, Object>> ambilSemuaHewan(String keyword) {
        return ambilSemuaHewan(keyword, null);
    }

    // FUNGSI UTAMA (Dengan Filter Spesies)
    public List<Map<String, Object>> ambilSemuaHewan(String keyword, Long filterSpesies) {
        String sql = "SELECT a.*, s.common_name AS species_name, " +
                "td.serial_number AS linked_device " +
                "FROM animals a " +
                "LEFT JOIN species s ON a.species_id = s.species_id " +
                "LEFT JOIN tracking_devices td ON a.animal_id = td.animal_id " +
                "WHERE 1=1 ";

        if (keyword != null && !keyword.isEmpty()) {
            sql += "AND (a.name LIKE '%" + keyword + "%' OR s.common_name LIKE '%" + keyword + "%') ";
        }

        if (filterSpesies != null) {
            sql += "AND a.species_id = " + filterSpesies + " ";
        }

        sql += "ORDER BY a.created_at DESC";

        return jdbcTemplate.queryForList(sql);
    }

    public void tambahHewan(String name, Long speciesId, String birthDate, String gender, Boolean isActive) {
        String sql = "INSERT INTO animals (name, species_id, birth_date, gender, is_active, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        Object finalDate = (birthDate == null || birthDate.isEmpty()) ? null : birthDate;
        jdbcTemplate.update(sql, name, speciesId, finalDate, gender, isActive);
    }

    public void updateHewan(Long id, String name, Long speciesId, String birthDate, String gender, Boolean isActive) {
        String sql = "UPDATE animals SET name = ?, species_id = ?, birth_date = ?, gender = ?, is_active = ?, updated_at = NOW() WHERE animal_id = ?";
        Object finalDate = (birthDate == null || birthDate.isEmpty()) ? null : birthDate;
        jdbcTemplate.update(sql, name, speciesId, finalDate, gender, isActive, id);
    }

    public void hapusHewan(Long id) {
        jdbcTemplate.update("DELETE FROM animals WHERE animal_id = ?", id);
    }
}