package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.MasterDataRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hewan")
@RequiredArgsConstructor
public class HewanController {

    private final HewanRepository hewanRepo;
    private final MasterDataRepository masterRepo;

    @GetMapping
    public String tampilkanHalamanHewan(HttpSession session, Model model,
                                        @RequestParam(required = false) String search,
                                        @RequestParam(required = false) Long filterSpesies) { // Parameter baru ditangkap di sini

        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // Memanggil repo yang sudah diupdate dengan 2 parameter
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(search, filterSpesies));
        model.addAttribute("daftarSpesies", masterRepo.ambilSemuaSpesies(null));

        // Simpan nilai agar tidak reset saat halaman di-refresh
        model.addAttribute("keywordPencarian", search);
        model.addAttribute("spesiesTerpilih", filterSpesies);

        return "hewan/daftar-hewan";
    }

    @PostMapping("/simpan")
    public String simpanAtauUpdateHewan(@RequestParam(required = false) Long id,
                                        @RequestParam String name,
                                        @RequestParam Long speciesId,
                                        @RequestParam String gender,
                                        @RequestParam(required = false) String birthDate,
                                        @RequestParam(required = false) Boolean isActive) {

        boolean statusAktif = isActive != null;

        if (id == null) {
            hewanRepo.tambahHewan(name, speciesId, birthDate, gender, statusAktif);
        } else {
            hewanRepo.updateHewan(id, name, speciesId, birthDate, gender, statusAktif);
        }

        return "redirect:/hewan";
    }

    @PostMapping("/hapus")
    public String hapusHewan(@RequestParam Long id) {
        hewanRepo.hapusHewan(id);
        return "redirect:/hewan";
    }
}