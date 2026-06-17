package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.RiwayatRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RiwayatController {

    private final RiwayatRepository riwayatRepo;
    private final HewanRepository hewanRepo;

    @GetMapping("/riwayat")
    public String tampilkanRiwayat(HttpSession session, Model model,
                                   @RequestParam(required = false) Long animalId,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate) {

        if (session.getAttribute("userRole") == null) return "redirect:/login";

        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        model.addAttribute("selectedAnimal", animalId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        if (animalId != null) {
            model.addAttribute("dataRiwayat", riwayatRepo.ambilRiwayatHewan(animalId, startDate, endDate));
        }

        return "tracking/riwayat";
    }

    // METHOD EXPORT SEKARANG ADA DI DALAM CLASS
    @GetMapping("/riwayat/export")
    public void exportCSV(jakarta.servlet.http.HttpServletResponse response,
                          @RequestParam(required = false) Long animalId,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate) throws java.io.IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"laporan_pelacakan_gps.csv\"");

        java.io.PrintWriter writer = response.getWriter();
        writer.println("Latitude,Longitude,Altitude (m),Waktu Rekam");

        if (animalId != null) {
            java.util.List<java.util.Map<String, Object>> data = riwayatRepo.ambilRiwayatHewan(animalId, startDate, endDate);
            for (java.util.Map<String, Object> baris : data) {
                writer.printf("%s,%s,%s,%s\n",
                        baris.get("latitude"),
                        baris.get("longitude"),
                        baris.get("altitude_meters") != null ? baris.get("altitude_meters") : "0",
                        baris.get("recorded_at"));
            }
        }
    }
}