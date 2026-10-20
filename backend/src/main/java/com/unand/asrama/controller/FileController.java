package com.unand.asrama.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin(origins = "*")
public class FileController {

    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> getUploadedFile(HttpServletRequest request) {
        try {
            String uri = request.getRequestURI();
            int idx = uri.indexOf("/uploads/");
            if (idx == -1) {
                return ResponseEntity.notFound().build();
            }
            String subPath = uri.substring(idx + "/uploads/".length());
            // Sanitize / prevent path traversal
            if (subPath.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            Path[] candidatePaths = new Path[] {
                Paths.get("uploads", subPath),
                Paths.get("backend", "uploads", subPath),
                Paths.get("..", "uploads", subPath),
                Paths.get("c:/Users/USER/Downloads/PRESENSI ASRAMA UNAND/uploads", subPath),
                Paths.get("c:/Users/USER/Downloads/PRESENSI ASRAMA UNAND/backend/uploads", subPath)
            };

            Path foundPath = null;
            for (Path p : candidatePaths) {
                if (Files.exists(p) && Files.isRegularFile(p)) {
                    foundPath = p;
                    break;
                }
            }

            if (foundPath == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] data = Files.readAllBytes(foundPath);
            ByteArrayResource resource = new ByteArrayResource(data);

            String fileName = foundPath.getFileName().toString().toLowerCase();
            String contentType = Files.probeContentType(foundPath);
            if (contentType == null) {
                if (fileName.endsWith(".png")) {
                    contentType = "image/png";
                } else if (fileName.endsWith(".pdf")) {
                    contentType = "application/pdf";
                } else {
                    contentType = "image/jpeg";
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + foundPath.getFileName().toString() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
