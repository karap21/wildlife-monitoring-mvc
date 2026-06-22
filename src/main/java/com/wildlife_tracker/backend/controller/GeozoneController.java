package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.GeozoneRepository;
import com.wildlife_tracker.backend.repository.HewanRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/geozone")
@RequiredArgsConstructor
public class GeozoneController {

    private final GeozoneRepository geozoneRepo;
    private final HewanRepository hewanRepo;

    @GetMapping
    public String tampilkanGeozone(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        model.addAttribute("daftarZona", geozoneRepo.ambilSemuaZona());
        return "tracking/geozone"; // Pastikan path ini sesuai
    }

    @PostMapping("/simpan")
    public String simpanGeozone(HttpSession session,
                                @RequestParam(required = false) Long zoneId,
                                @RequestParam String name,
                                @RequestParam String polygonCoordinates,
                                @RequestParam(required = false) List<Long> animalIds) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // LOGIKA BARU: Cek jika zoneId ada nilainya = Edit. Jika kosong = Buat Baru
        if (zoneId == null) {
            geozoneRepo.simpanZonaBaru(name, polygonCoordinates, animalIds);
        } else {
            geozoneRepo.updateZona(zoneId, name, polygonCoordinates, animalIds);
        }
        return "redirect:/geozone";
    }

    @PostMapping("/hapus")
    public String hapusGeozone(HttpSession session, @RequestParam Long zoneId) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";
        geozoneRepo.hapusZona(zoneId);
        return "redirect:/geozone";
    }
}