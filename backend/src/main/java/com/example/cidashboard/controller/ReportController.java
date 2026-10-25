package com.example.cidashboard.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    private final Path reportDir = Path.of("target", "surefire-reports").toAbsolutePath().normalize();

    @GetMapping
    public Map<String, Object> reports() throws Exception {
        java.util.List<String> files;
        if (Files.isDirectory(reportDir)) {
            try (var paths = Files.list(reportDir)) {
                files = paths.filter(Files::isRegularFile).map(p -> p.getFileName().toString())
                        .filter(n -> n.endsWith(".txt") || n.endsWith(".xml")).sorted().toList();
            }
        } else {
            files = java.util.List.of();
        }
        return Map.of("mode", "BACKEND", "available", !files.isEmpty(), "files", files);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        if (!filename.matches("[A-Za-z0-9_.-]+\\.(txt|xml)")) return ResponseEntity.notFound().build();
        Path file = reportDir.resolve(filename).normalize();
        if (!file.startsWith(reportDir) || !Files.isRegularFile(file)) return ResponseEntity.notFound().build();
        var headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok().headers(headers).body(new FileSystemResource(file));
    }
}
