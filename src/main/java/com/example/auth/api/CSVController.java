package com.example.auth.api;

import com.example.auth.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/csv")
public class CSVController {
    private final StatisticsService statisticsService;

    @GetMapping("/download-csv")
    public ResponseEntity<Resource> downloadCSV(@RequestParam("start") LocalDateTime start, @RequestParam("finish") LocalDateTime finish) throws IOException {
        try {
            File csvFile = statisticsService.generateCVS(start, finish);
            Path path = csvFile.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFile.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvFile.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (IOException e) {
            throw new IOException("failed to download csv");
        }
    }
}