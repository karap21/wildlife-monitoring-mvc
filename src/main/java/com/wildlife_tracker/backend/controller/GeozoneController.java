package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.GeozoneRepository;
import com.wildlife_tracker.backend.repository.HewanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GeozoneController {

    private final GeozoneRepository geozoneRepo;
    private final HewanRepository hewanRepo;

    @GetMapping("/geozone")
    public String tampilkanGeozone(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        model.addAttribute("daftarZona", geozoneRepo.ambilSemuaZona());
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        return "tracking/geozone";
    }

    @PostMapping("/geozone/simpan")
    public String simpanGeozone(@RequestParam String name,
                                @RequestParam String polygonCoordinates,
                                @RequestParam(required = false) List<Long> animalIds) {

        geozoneRepo.simpanZona(name, polygonCoordinates, animalIds);
        return "redirect:/geozone";
    }

    @PostMapping("/geozone/hapus")
    public String hapusGeozone(@RequestParam Long zoneId) {
        geozoneRepo.hapusZona(zoneId);
        return "redirect:/geozone";
    }
}