package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.AkunRepository;
import com.wildlife_tracker.backend.repository.RoleRepository;
import com.wildlife_tracker.backend.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AkunRepository akunRepo;
    private final RoleRepository roleRepo;

    @GetMapping("/rbac")
    public String tampilkanHalamanRBAC(HttpSession session, Model model) {
        if (!"ADMINISTRATOR".equals(session.getAttribute("userRole"))) {
            return "redirect:/";
        }
        model.addAttribute("daftarAkun", akunRepo.ambilSemuaAkun());
        model.addAttribute("daftarRole", roleRepo.ambilSemuaRole());
        return "rbac";
    }

    @PostMapping("/akun/simpan")
    public String simpanAkun(@RequestParam String namaLengkap,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam Long roleId,
                             RedirectAttributes redirectAttributes) { // Ditambah parameter penangkap error
        try {
            String roleStr = (roleId == 1) ? "ADMINISTRATOR" : "PENELITI";
            String passwordEnkripsi = PasswordUtil.enkripsi(password);
            akunRepo.tambahAkun(namaLengkap, email, passwordEnkripsi, roleStr);
        } catch (Exception e) {
            // Mencegah Error 500 jika email sudah ada
            redirectAttributes.addFlashAttribute("error", "Gagal! Email tersebut sudah terdaftar di sistem.");
        }
        return "redirect:/admin/rbac";
    }

    @PostMapping("/akun/hapus")
    public String hapusAkun(@RequestParam Long id) {
        akunRepo.hapusAkun(id);
        return "redirect:/admin/rbac";
    }

    @PostMapping("/role/simpan")
    public String simpanRole(@RequestParam String namaRole, RedirectAttributes redirectAttributes) {
        try {
            roleRepo.tambahRole(namaRole);
        } catch (Exception e) {
            // Mencegah Error 500 jika nama Role sudah ada
            redirectAttributes.addFlashAttribute("error", "Gagal! Nama Role tersebut sudah ada.");
        }
        return "redirect:/admin/rbac";
    }
}