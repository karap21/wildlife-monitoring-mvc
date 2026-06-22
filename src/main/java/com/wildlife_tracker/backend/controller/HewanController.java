package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.MasterDataRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/hewan")
@RequiredArgsConstructor
public class HewanController {

    private final HewanRepository hewanRepo;
    private final MasterDataRepository masterRepo;

    @GetMapping
    public String tampilkanHalamanHewan(HttpSession session, Model model,
                                        @RequestParam(required = false) String search,
                                        @RequestParam(required = false) List<Long> filterSpesies) { // DIUBAH MENJADI LIST

        if (!"ADMINISTRATOR".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }

        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(search, filterSpesies));
        model.addAttribute("daftarSpesies", masterRepo.ambilSemuaSpesies(null));
        model.addAttribute("daftarStatus", masterRepo.ambilSemuaStatusKonservasi());

        model.addAttribute("keywordPencarian", search);
        model.addAttribute("spesiesTerpilih", filterSpesies); // Menyimpan state checklist

        return "hewan/daftar-hewan";
    }

    @PostMapping("/simpan")
    public String simpanAtauUpdateHewan(@RequestParam(required = false) Long id,
                                        @RequestParam String name,
                                        @RequestParam Long speciesId,
                                        @RequestParam(required = false) String customSpeciesName,
                                        @RequestParam(required = false) String customScientificName,
                                        @RequestParam(required = false) Long customStatusId,
                                        @RequestParam String gender,
                                        @RequestParam(required = false) String birthDate,
                                        @RequestParam(required = false) Boolean isActive,
                                        RedirectAttributes redirectAttributes) {
        try {
            boolean statusAktif = isActive != null;
            Long finalSpeciesId = speciesId;

            if (speciesId == -1L && customSpeciesName != null && !customSpeciesName.isEmpty()) {
                finalSpeciesId = masterRepo.tambahSpesiesGetId(customSpeciesName, customScientificName, customStatusId);
            }

            if (id == null) {
                hewanRepo.tambahHewan(name, finalSpeciesId, birthDate, gender, statusAktif);
            } else {
                hewanRepo.updateHewan(id, name, finalSpeciesId, birthDate, gender, statusAktif);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan sistem saat menyimpan data hewan.");
        }
        return "redirect:/hewan";
    }

    @PostMapping("/hapus")
    public String hapusHewan(@RequestParam Long id) {
        hewanRepo.hapusHewan(id);
        return "redirect:/hewan";
    }
}