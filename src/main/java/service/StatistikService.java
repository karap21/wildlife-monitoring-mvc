package com.wildlife_tracker.backend.service;

import java.util.List;
import java.util.Map;

public interface StatistikService {
    // Fungsi untuk halaman Statistik
    List<Map<String, Object>> getFormatGrafikSpesies();

    // Fungsi-fungsi untuk Laporan PDF
    int getTotalSatwa();
    int getTotalPerangkat();
    String generateTeksAnalisisAI();
    List<Map<String, Object>> getStatistikSpesiesLaporan();
    List<Map<String, Object>> getDetailKondisiSatwaLaporan();
}