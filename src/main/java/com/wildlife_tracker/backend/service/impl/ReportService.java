package com.wildlife_tracker.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.OutputStream;
import java.util.Map;

@Service
public class ReportService {

    private final TemplateEngine templateEngine;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=}")
    private String apiUrl;

    public ReportService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.restTemplate = new RestTemplate();
    }

    public String dapatkanAnalisisAI(long totalHewan, long totalAlat) {
        if (apiKey == null || apiKey.trim().isEmpty() || "MASUKKAN_API_KEY_ANDA_DISINI".equals(apiKey)) {
            return "Sistem Analisis AI saat ini dalam mode offline. Berdasarkan data tercatat, sistem melacak " + totalHewan + " ekor satwa dengan " + totalAlat + " perangkat GPS aktif. Semua instrumen pelacakan berjalan stabil di zona konservasi.";
        }

        try {
            String prompt = String.format("Kamu adalah ahli konservasi satwa. Buat 1 paragraf (maksimal 60 kata) ringkasan eksekutif profesional. Data saat ini: %d satwa dipantau, %d perangkat GPS aktif. Berikan tone optimis dan saintifik. Langsung berikan isinya tanpa salam pembuka.", totalHewan, totalAlat);

            String requestBody = "{\"contents\":[{\"parts\":[{\"text\": \"" + prompt + "\"}]}]}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(apiUrl + apiKey, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText().replace("\n", " ");

        } catch (Exception e) {
            return "Terjadi gangguan saat menghubungi satelit AI. Data terpantau aman: " + totalHewan + " satwa dan " + totalAlat + " perangkat aktif.";
        }
    }

    public void generatePdfReport(Map<String, Object> data, OutputStream outputStream) throws Exception {
        Context context = new Context();
        context.setVariables(data);
        String htmlContent = templateEngine.process("tracking/laporan-pdf", context);

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, "/");
        builder.toStream(outputStream);
        builder.run();
    }
}