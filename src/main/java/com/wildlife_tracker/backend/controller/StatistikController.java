package com.wildlife_tracker.backend.controller;

import com.wildlife_tracker.backend.repository.StatistikRepository;
import com.wildlife_tracker.backend.service.ReportService; // Import Service AI kita
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatistikController {

    private final StatistikRepository statistikRepo;
    private final ReportService reportService; // Panggil mesin pembuat PDF

    // 1. FUNGSI LAMA: Menampilkan Halaman Statistik (Grafik)
    @GetMapping("/statistik")
    public String tampilkanStatistik(HttpSession session, Model model) {
        // Enkapsulasi RBAC: Hanya PENELITI yang boleh mengakses laporan ini
        if (!"PENELITI".equals(session.getAttribute("userRole"))) return "redirect:/dasbor";

        model.addAttribute("dataGrafik", statistikRepo.ambilDataPopulasiSpesies());
        return "tracking/statistik";
    }

    // 2. FUNGSI BARU: Mengunduh Laporan AI berwujud PDF
    @GetMapping("/statistik/cetak-pdf")
    public void cetakLaporanCerdas(jakarta.servlet.http.HttpServletResponse response) throws Exception {

        // Tarik Data Statistik Dasar
        long totalHewan = statistikRepo.count("animals");
        long totalAlat = statistikRepo.count("tracking_devices");

        // Minta AI membuat kesimpulan
        String kesimpulanAI = reportService.dapatkanAnalisisAI(totalHewan, totalAlat);

        // Siapkan keranjang data untuk dikirim ke template PDF
        java.util.Map<String, Object> dataLaporan = new java.util.HashMap<>();
        dataLaporan.put("tanggalLaporan", java.time.LocalDate.now().toString());
        dataLaporan.put("totalSatwa", totalHewan);
        dataLaporan.put("totalPerangkat", totalAlat);
        dataLaporan.put("analisisCerdas", kesimpulanAI);

        // Perintahkan browser mengunduhnya sebagai file PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"WildTrack_AI_Report.pdf\"");

        // Proses pembuatan PDF
        reportService.generatePdfReport(dataLaporan, response.getOutputStream());
    }
}