package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SimulatorController {

    private final HewanRepository hewanRepo;

    // 1. Menampilkan Halaman Simulator
    @GetMapping("/simulator")
    public String tampilkanSimulator(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // Tarik semua daftar satwa untuk dimasukkan ke dropdown simulator
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        return "tracking/simulator";
    }

    // 2. Menerima Titik Kordinat Baru dari AJAX (Javascript Simulasi)
    @PostMapping("/simulator/kirim-titik")
    @ResponseBody // Langsung balas dengan JSON agar halaman tidak me-refresh
    public ResponseEntity<Map<String, Object>> terimaTitikSimulasi(
            @RequestParam Long animalId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        Map<String, Object> response = new HashMap<>();
        try {
            // PENTING: Di sini idealnya Anda memanggil repository untuk INSERT ke tabel riwayat/history
            // Contoh jika Anda punya RiwayatRepository:
            // riwayatRepo.simpanTitik(animalId, latitude, longitude);

            // Catatan: Jika titik ini berada di luar batas Geozone, Anda juga bisa menyisipkan
            // logika untuk INSERT ke tabel 'peringatan' (Alert) di sini.

            response.put("status", "success");
            response.put("pesan", "Titik kordinat berhasil dicatat ke database.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("pesan", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}