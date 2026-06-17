package com.wildlife_tracker.backend.controller;

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

    @GetMapping("/")
    public String tampilkanDashboard(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // Mengambil data dari repository baru
        model.addAttribute("totalHewan", statistikRepo.count("animals"));
        model.addAttribute("satwaAktif", statistikRepo.countActiveAnimals());
        model.addAttribute("totalPerangkat", statistikRepo.count("tracking_devices"));

        return "dashboard";
    }
}