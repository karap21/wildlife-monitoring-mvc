package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.service.impl.AkunService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AkunController {

    private final AkunService akunService;

    @GetMapping("/login")
    public String tampilkanHalamanLogin(HttpSession session) {
        if (session.getAttribute("userRole") != null) return "redirect:/";
        return "login";
    }

    @PostMapping("/login")
    public String prosesLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session, Model model) {

        Map<String, Object> user = akunService.prosesLogin(email, password);

        if (user != null) {
            session.setAttribute("userId", user.get("id"));
            session.setAttribute("userName", user.get("nama_lengkap"));
            session.setAttribute("userRole", user.get("role"));
            return "redirect:/";
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