package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.PerangkatRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/perangkat")
@RequiredArgsConstructor
public class PerangkatController {

    private final PerangkatRepository perangkatRepo;
    private final HewanRepository hewanRepo;

    // FUNGSI 1: Tampil Halaman dengan Cek Permission (BUKAN Role)
    @GetMapping
    public String tampilkanPerangkat(HttpSession session, Model model) {
        List<String> izin = (List<String>) session.getAttribute("userPermissions");

        // Jika tidak punya izin 'manage_devices', tendang!
        if (izin == null || !izin.contains("manage_devices")) {
            // (Fallback sementara ke cek Role lama jika tabel permission belum siap)
            String role = (String) session.getAttribute("userRole");
            if (role == null || "PENELITI".equals(role)) return "redirect:/";
        }

        model.addAttribute("daftarPerangkat", perangkatRepo.ambilSemuaPerangkat());
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        return "hewan/perangkat";
    }

    @PostMapping("/simpan")
    public String simpanPerangkat(HttpSession session,
                                  @RequestParam(required = false) Long deviceId,
                                  @RequestParam Long animalId,
                                  @RequestParam String modelType,
                                  @RequestParam String serialNumber,
                                  @RequestParam Integer batteryLife,
                                  @RequestParam(required = false) String installDate,
                                  RedirectAttributes redirectAttributes) {

        List<String> izin = (List<String>) session.getAttribute("userPermissions");
        if (izin == null || !izin.contains("manage_devices")) {
            String role = (String) session.getAttribute("userRole");
            if (role == null || "PENELITI".equals(role)) return "redirect:/";
        }

        try {
            if (deviceId == null) {
                perangkatRepo.tambahPerangkat(animalId, modelType, serialNumber, batteryLife, installDate);
            } else {
                perangkatRepo.updatePerangkat(deviceId, animalId, modelType, serialNumber, batteryLife, installDate);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Gagal menyimpan data perangkat.");
        }
        return "redirect:/perangkat";
    }

    @PostMapping("/hapus")
    public String hapusPerangkat(HttpSession session, @RequestParam Long id) {
        List<String> izin = (List<String>) session.getAttribute("userPermissions");
        if (izin == null || !izin.contains("manage_devices")) {
            String role = (String) session.getAttribute("userRole");
            if (role == null || "PENELITI".equals(role)) return "redirect:/";
        }

        perangkatRepo.hapusPerangkat(id);
        return "redirect:/perangkat";
    }

    @GetMapping("/export/csv")
    public void exportCSV(HttpSession session, HttpServletResponse response) throws Exception {
        List<String> izin = (List<String>) session.getAttribute("userPermissions");
        if (izin == null || !izin.contains("export_data")) {
            String role = (String) session.getAttribute("userRole");
            if ("PENELITI".equals(role)) return;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Data_Perangkat_GPS_WildTrack.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Device ID,Model GPS,Nomor Seri,Ketahanan Baterai (Bulan),Status,Hewan Terkait");

        List<Map<String, Object>> data = perangkatRepo.ambilSemuaPerangkat();
        for (Map<String, Object> baris : data) {
            writer.printf("%s,%s,%s,%s,%s,%s\n",
                    baris.get("device_id"),
                    baris.get("model"),
                    baris.get("serial_number"),
                    baris.get("battery_life_months"),
                    baris.get("status"),
                    baris.get("animal_name") != null ? baris.get("animal_name") : "Belum Dipasangkan");
        }
    }

    @GetMapping("/export/json")
    @ResponseBody
    public List<Map<String, Object>> exportJSON(HttpSession session, HttpServletResponse response) {
        List<String> izin = (List<String>) session.getAttribute("userPermissions");
        if (izin == null || !izin.contains("export_data")) {
            String role = (String) session.getAttribute("userRole");
            if ("PENELITI".equals(role)) return null;
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"Data_Perangkat_GPS_WildTrack.json\"");
        return perangkatRepo.ambilSemuaPerangkat();
    }
}