package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.PelacakanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PelacakanController {

    private final PelacakanRepository pelacakanRepo;

    @GetMapping("/pelacakan")
    public String tampilkanPeta(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // Melempar data lokasi ke Leaflet HTML
        model.addAttribute("dataLokasi", pelacakanRepo.ambilLokasiTerakhirSatwa());

        return "tracking/peta";
    }
}