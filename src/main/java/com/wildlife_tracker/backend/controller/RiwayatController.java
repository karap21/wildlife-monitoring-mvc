package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.RiwayatRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RiwayatController {

    private final RiwayatRepository riwayatRepo;
    private final HewanRepository hewanRepo;

    @GetMapping("/riwayat")
    public String tampilkanRiwayat(HttpSession session, Model model,
                                   @RequestParam(required = false) List<Long> animalIds,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate) {

        if (session.getAttribute("userRole") == null) return "redirect:/login";

        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        model.addAttribute("selectedAnimals", animalIds); // Mengirim state checklist ke UI
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        if (animalIds != null && !animalIds.isEmpty()) {
            model.addAttribute("dataRiwayat", riwayatRepo.ambilRiwayatHewan(animalIds, startDate, endDate));
        }

        return "tracking/riwayat";
    }

    @GetMapping("/riwayat/export")
    public void exportCSV(jakarta.servlet.http.HttpServletResponse response,
                          @RequestParam(required = false) List<Long> animalIds,
                          @RequestParam(required = false) String startDate,
                          @RequestParam(required = false) String endDate) throws java.io.IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"laporan_pelacakan_gps.csv\"");

        java.io.PrintWriter writer = response.getWriter();
        // Kolom ditambah "Nama Hewan" agar laporan CSV bisa membedakan koordinat
        writer.println("Nama Hewan,Latitude,Longitude,Altitude (m),Waktu Rekam");

        if (animalIds != null && !animalIds.isEmpty()) {
            java.util.List<java.util.Map<String, Object>> data = riwayatRepo.ambilRiwayatHewan(animalIds, startDate, endDate);
            for (java.util.Map<String, Object> baris : data) {
                writer.printf("%s,%s,%s,%s,%s\n",
                        baris.get("animal_name"),
                        baris.get("latitude"),
                        baris.get("longitude"),
                        baris.get("altitude_meters") != null ? baris.get("altitude_meters") : "0",
                        baris.get("recorded_at"));
            }
        }
    }
}