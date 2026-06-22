package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.MasterDataRepository;
import com.wildlife_tracker.backend.repository.PeringatanRepository;
import com.wildlife_tracker.backend.repository.StatistikRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StatistikRepository statistikRepo;
    private final MasterDataRepository masterRepo;
    // TAMBAHAN: Menginjeksi Engine Peringatan
    private final PeringatanRepository peringatanRepo;

    @GetMapping("/")
    public String tampilkanDashboard(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // 1. PICU MESIN PERINGATAN CERDAS SEBELUM RENDER DASHBOARD
        peringatanRepo.generateSmartAlerts();

        // 2. AMBIL SEMUA METRIK KOTAK ATAS
        model.addAttribute("totalHewan", statistikRepo.count("animals"));
        model.addAttribute("satwaAktif", statistikRepo.countActiveAnimals());
        model.addAttribute("totalPerangkat", statistikRepo.count("tracking_devices"));

        // Memasukkan metrik Zona dan Peringatan ke HTML (ini yang membuat angkanya tidak nol lagi)
        model.addAttribute("totalZona", statistikRepo.count("geozones"));
        model.addAttribute("totalAlert", statistikRepo.countUnreadAlerts());

        // 3. KIRIM DATA DISTRIBUSI SPESIES
        model.addAttribute("statistikSpesies", masterRepo.getStatistikSpesiesLaporan());

        return "dashboard";
    }
}