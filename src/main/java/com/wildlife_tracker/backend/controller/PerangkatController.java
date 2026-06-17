package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.PerangkatRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/perangkat")
@RequiredArgsConstructor
public class PerangkatController {

    private final PerangkatRepository perangkatRepo;
    private final HewanRepository hewanRepo;

    @GetMapping
    public String tampilkanHalamanPerangkat(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        model.addAttribute("daftarPerangkat", perangkatRepo.ambilSemuaPerangkat());
        // Memanggil daftar hewan agar bisa dipilih di form perangkat
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));

        return "hewan/perangkat";
    }

    @PostMapping("/simpan")
    public String simpanPerangkat(@RequestParam(required = false) Long id,
                                  @RequestParam String model,
                                  @RequestParam(required = false) String customModel,
                                  @RequestParam String serialNumber,
                                  @RequestParam(required = false) Long animalId,
                                  @RequestParam(required = false, defaultValue = "12") Integer batteryLifeMonths,
                                  @RequestParam(required = false) String installationDate) {

        // Logika custom model dari React dipindahkan ke Java murni
        String finalModel = "__custom__".equals(model) ? customModel : model;

        if (id == null) {
            perangkatRepo.tambahPerangkat(animalId, finalModel, serialNumber, batteryLifeMonths, installationDate);
        } else {
            perangkatRepo.updatePerangkat(id, animalId, finalModel, serialNumber, batteryLifeMonths, installationDate);
        }

        return "redirect:/perangkat";
    }

    @PostMapping("/hapus")
    public String hapusPerangkat(@RequestParam Long id) {
        perangkatRepo.hapusPerangkat(id);
        return "redirect:/perangkat";
    }
}