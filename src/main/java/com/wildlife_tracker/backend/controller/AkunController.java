package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.model.AkunPegawai;
import com.wildlife_tracker.backend.repository.RoleRepository;
import com.wildlife_tracker.backend.service.impl.AkunService;
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
public class AkunController {

    private final AkunService akunService;
    // Inject RoleRepository untuk menarik Permission saat Login
    private final RoleRepository roleRepo;

    @GetMapping("/login")
    public String tampilkanHalamanLogin(HttpSession session) {
        if (session.getAttribute("userRole") != null) return "redirect:/";
        return "login";
    }

    @PostMapping("/login")
    public String prosesLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session, Model model) {

        AkunPegawai user = akunService.prosesLogin(email, password);

        if (user != null) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getNamaLengkap());

            // Simpan Nama Role (Contoh: "ADMINISTRATOR" atau "ECOLOGY_RESEARCHER")
            String userRole = user.getRole();
            session.setAttribute("userRole", userRole);

            // LOGIKA BARU: Tarik dan simpan Permission ke Session
            List<String> userPermissions = roleRepo.ambilHakAksesOlehRole(userRole);
            session.setAttribute("userPermissions", userPermissions);

            return user.getHalamanAwal();
        } else {
            model.addAttribute("error", "Email atau password salah!");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}