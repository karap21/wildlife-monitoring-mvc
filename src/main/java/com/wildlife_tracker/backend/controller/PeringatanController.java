package com.wildlife_tracker.backend.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PeringatanController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/peringatan")
    public String tampilkanPeringatan(Model model) {
        // Kita suruh MySQL yang melakukan format tanggalnya menggunakan DATE_FORMAT
        String sql = "SELECT s.*, a.name AS animal_name, " +
                "DATE_FORMAT(s.created_at, '%d %b %Y, %H:%i') AS formatted_time " +
                "FROM system_alerts s " +
                "LEFT JOIN animals a ON s.animal_id = a.animal_id " +
                "ORDER BY s.created_at DESC";

        model.addAttribute("daftarAlert", jdbcTemplate.queryForList(sql));
        return "tracking/peringatan";
    }

    @PostMapping("/peringatan/tandai-dibaca")
    public String tandaiDibaca(@RequestParam Long alertId) {
        jdbcTemplate.update("UPDATE system_alerts SET is_read = TRUE WHERE alert_id = ?", alertId);
        return "redirect:/peringatan";
    }
}