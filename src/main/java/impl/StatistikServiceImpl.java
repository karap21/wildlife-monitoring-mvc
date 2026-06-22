package com.wildlife_tracker.backend.service.impl;

import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.MasterDataRepository;
import com.wildlife_tracker.backend.repository.PerangkatRepository;
import com.wildlife_tracker.backend.service.StatistikService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatistikServiceImpl implements StatistikService {

    private final MasterDataRepository masterRepo;
    private final HewanRepository hewanRepo;
    private final PerangkatRepository perangkatRepo;

    @Override
    public List<Map<String, Object>> getFormatGrafikSpesies() {
        List<Map<String, Object>> dataSpesies = masterRepo.getStatistikSpesiesLaporan();
        List<Map<String, Object>> dataGrafik = new ArrayList<>();

        for (Map<String, Object> baris : dataSpesies) {
            Map<String, Object> grafikRow = new HashMap<>();
            grafikRow.put("label", baris.get("common_name"));
            grafikRow.put("value", baris.get("total_hewan"));
            dataGrafik.add(grafikRow);
        }
        return dataGrafik;
    }

    @Override
    public int getTotalSatwa() {
        return hewanRepo.ambilSemuaHewan(null).size();
    }

    @Override
    public int getTotalPerangkat() {
        return perangkatRepo.ambilSemuaPerangkat().size();
    }

    @Override
    public String generateTeksAnalisisAI() {
        int totalSatwa = getTotalSatwa();
        int totalPerangkat = getTotalPerangkat();
        return "Sistem Analisis AI saat ini dalam mode offline. Berdasarkan data rekam jejak, sistem secara aktif melacak "
                + totalSatwa + " ekor satwa menggunakan " + totalPerangkat
                + " perangkat GPS. Sebagian besar instrumen pemantauan geofence beroperasi dengan stabil.";
    }

    @Override
    public List<Map<String, Object>> getStatistikSpesiesLaporan() {
        return masterRepo.getStatistikSpesiesLaporan();
    }

    @Override
    public List<Map<String, Object>> getDetailKondisiSatwaLaporan() {
        return masterRepo.getDetailKondisiSatwaLaporan();
    }
}