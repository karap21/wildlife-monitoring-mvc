package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.MasterDataRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/spesies")
public class SpesiesController {

    private final MasterDataRepository masterRepo;

    @GetMapping
    public String tampilkanHalamanSpesies(HttpSession session, Model model,
                                          @RequestParam(required = false) String search) {
        // PERBAIKAN PENTING: Penguncian Strict RBAC (Hanya Admin yang boleh masuk)
        if (!"ADMINISTRATOR".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }

        model.addAttribute("daftarSpesies", masterRepo.ambilSemuaSpesies(search));
        model.addAttribute("daftarStatus", masterRepo.ambilSemuaStatusKonservasi());
        model.addAttribute("keywordPencarian", search);

        return "hewan/spesies";
    }

    @PostMapping("/tambah")
    public String tambahSpesies(@RequestParam String commonName,
                                @RequestParam(required = false) String scientificName,
                                @RequestParam(required = false) Long statusId,
                                @RequestParam(required = false) String description,
                                RedirectAttributes redirectAttributes) {
        try {
            masterRepo.tambahSpesies(commonName, scientificName, statusId, description);
        } catch (Exception e) {
            // Menangkap error jika mencoba memasukkan Spesies yang namanya sudah ada
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan! Nama spesies tersebut mungkin sudah ada.");
        }
        return "redirect:/spesies";
    }

    @PostMapping("/hapus")
    public String hapusSpesies(@RequestParam Long id) {
        masterRepo.hapusSpesies(id);
        return "redirect:/spesies";
    }
}