package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.AkunRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AkunRepository akunRepo;

    @GetMapping("/rbac")
    public String tampilkanHalamanRBAC(HttpSession session, Model model) {
        if (!"ADMINISTRATOR".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        model.addAttribute("daftarAkun", akunRepo.ambilSemuaAkun());
        return "admin/rbac";
    }

    @PostMapping("/akun/simpan")
    public String simpanAkun(@RequestParam String namaLengkap,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam Long roleId) { // Ini Long

        // KONVERSI DI SINI: Ubah Long ke String agar cocok dengan tabel akun Anda
        String roleStr = String.valueOf(roleId);

        // Atau jika sistem Anda menggunakan mapping angka ke teks:
        // String roleStr = (roleId == 1) ? "ADMINISTRATOR" : "PENELITI";

        akunRepo.tambahAkun(namaLengkap, email, password, roleStr);
        return "redirect:/admin/rbac";
    }

    @PostMapping("/akun/hapus")
    public String hapusAkun(@RequestParam Long id) {
        akunRepo.hapusAkun(id);
        return "redirect:/admin/rbac";
    }
}