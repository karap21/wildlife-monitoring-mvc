package com.wildlife_tracker.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wildlife_tracker.backend.repository.GeozoneRepository;
import com.wildlife_tracker.backend.repository.HewanRepository;
import com.wildlife_tracker.backend.repository.PerangkatRepository;
import com.wildlife_tracker.backend.repository.SimulatorRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SimulatorController {

    private final HewanRepository hewanRepo;
    private final PerangkatRepository perangkatRepo;
    private final SimulatorRepository simulatorRepo;
    // TAMBAHAN: Inject Geozone
    private final GeozoneRepository geozoneRepo;

    @GetMapping("/simulator")
    public String tampilkanSimulator(HttpSession session, Model model) {
        if (session.getAttribute("userRole") == null) return "redirect:/login";
        model.addAttribute("daftarHewan", hewanRepo.ambilSemuaHewan(null));
        model.addAttribute("riwayatEksisting", simulatorRepo.ambilRiwayatEksisting());

        // TAMBAHAN: Kirim data Zona ke UI Simulator
        model.addAttribute("daftarZona", geozoneRepo.ambilSemuaZona());

        return "tracking/simulator";
    }

    @PostMapping("/simulator/kirim-titik")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> terimaTitikSimulasi(
            @RequestParam Long animalId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        Map<String, Object> response = new HashMap<>();
        try {
            Long deviceId = perangkatRepo.cariDeviceIdOlehAnimalId(animalId);
            if (deviceId == null) {
                response.put("status", "error");
                response.put("pesan", "Hewan belum dipasangi GPS.");
                return ResponseEntity.badRequest().body(response);
            }
            simulatorRepo.tembakSinyalGps(deviceId, latitude, longitude);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("pesan", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/simulator/simpan-json")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> simpanFileJson(@RequestBody List<Map<String, Object>> dataSimulasi) {
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File fileJson = new File("Laporan_Simulasi_Satelit.json");
            mapper.writerWithDefaultPrettyPrinter().writeValue(fileJson, dataSimulasi);
            response.put("status", "success");
            response.put("pesan", "Backup ke: " + fileJson.getAbsolutePath());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/simulator/reset")
    public String resetSimulator() {
        simulatorRepo.resetSistemSimulasi();
        return "redirect:/simulator";
    }
}