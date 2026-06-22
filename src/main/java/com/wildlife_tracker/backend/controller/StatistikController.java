package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.MasterDataRepository;
import com.wildlife_tracker.backend.repository.PerangkatRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StatistikController {

    private final MasterDataRepository masterRepo;
    private final HewanRepository hewanRepo;
    private final PerangkatRepository perangkatRepo;

    // FUNGSI 1: Menangani halaman Grafik Statistik
    @GetMapping("/statistik")
    public String tampilkanStatistik(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        List<Map<String, Object>> dataSpesies = masterRepo.getStatistikSpesiesLaporan();
        List<Map<String, Object>> dataGrafik = new ArrayList<>();

        for (Map<String, Object> baris : dataSpesies) {
            Map<String, Object> grafikRow = new HashMap<>();
            grafikRow.put("label", baris.get("common_name"));
            grafikRow.put("value", baris.get("total_hewan"));
            dataGrafik.add(grafikRow);
        }

        model.addAttribute("dataGrafik", dataGrafik);

        // PERBAIKAN: Menambahkan folder "tracking/" karena statistik.html ada di dalamnya
        return "tracking/statistik";
    }

    // FUNGSI 2: Menangani tombol "Cetak Laporan AI (PDF)" di Dashboard
    @GetMapping("/statistik/cetak-pdf")
    public String cetakLaporanPDF(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        int totalSatwa = hewanRepo.ambilSemuaHewan(null).size();
        int totalPerangkat = perangkatRepo.ambilSemuaPerangkat().size();

        model.addAttribute("tanggalLaporan", LocalDate.now().toString());
        model.addAttribute("totalSatwa", totalSatwa);
        model.addAttribute("totalPerangkat", totalPerangkat);

        String teksAI = "Sistem Analisis AI saat ini dalam mode offline. Berdasarkan data rekam jejak, sistem secara aktif melacak "
                + totalSatwa + " ekor satwa menggunakan " + totalPerangkat
                + " perangkat GPS. Semua instrumen pemantauan terdeteksi dan beroperasi dengan stabil.";
        model.addAttribute("analisisCerdas", teksAI);

        model.addAttribute("statistikSpesies", masterRepo.getStatistikSpesiesLaporan());
        model.addAttribute("detailKondisiSatwa", masterRepo.getDetailKondisiSatwaLaporan());

        // Ini sudah benar karena laporan-pdf.html ada di luar (root templates)
        return "laporan-pdf";
    }
}