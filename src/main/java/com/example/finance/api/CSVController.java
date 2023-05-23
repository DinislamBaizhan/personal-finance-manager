package com.example.finance.api;

import com.example.finance.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Transaction to csv controller", description = "csv management")
public class CSVController {
    private final StatisticsService statisticsService;

    @GetMapping("/download-csv")
    @Operation(summary = "Download", description = "Download transaction data in csv format.")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully.")
    public ResponseEntity<Resource> downloadCSV(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("finish") LocalDateTime finish
    ) throws IOException {
        try {
            File csvFile = statisticsService.generateCSV(start, finish);
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