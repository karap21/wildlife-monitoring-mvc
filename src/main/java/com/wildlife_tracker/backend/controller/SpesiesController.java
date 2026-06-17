package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.MasterDataRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/spesies") // Semua rute akan berawalan /spesies
public class SpesiesController {

    private final MasterDataRepository masterRepo;

    // 1. Menampilkan Halaman Tabel Spesies
    @GetMapping
    public String tampilkanHalamanSpesies(HttpSession session,
                                          Model model,
                                          @RequestParam(required = false) String search) {
        // Keamanan: Cek Login
        if (session.getAttribute("userRole") == null) return "redirect:/login";

        // Ambil data untuk dilempar ke HTML
        model.addAttribute("daftarSpesies", masterRepo.ambilSemuaSpesies(search));
        model.addAttribute("daftarStatus", masterRepo.ambilSemuaStatusKonservasi()); // Untuk opsi form
        model.addAttribute("keywordPencarian", search); // Agar teks pencarian tidak hilang

        return "hewan/spesies"; // Render file spesies.html
    }

    // 2. Memproses Form Tambah Spesies (Langsung ditangkap dari HTML)
    @PostMapping("/tambah")
    public String tambahSpesies(@RequestParam String commonName,
                                @RequestParam(required = false) String scientificName,
                                @RequestParam(required = false) Long statusId,
                                @RequestParam(required = false) String description) {

        masterRepo.tambahSpesies(commonName, scientificName, statusId, description);
        return "redirect:/spesies"; // Refresh halaman otomatis
    }

    // 3. Memproses Penghapusan
    @PostMapping("/hapus")
    public String hapusSpesies(@RequestParam Long id) {
        masterRepo.hapusSpesies(id);
        return "redirect:/spesies";
    }
}