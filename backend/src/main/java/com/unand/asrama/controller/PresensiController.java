package com.unand.asrama.controller;

import com.unand.asrama.dto.ApiResponse;
import com.unand.asrama.dto.PresensiRequest;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.service.PresensiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presensi")
@CrossOrigin(origins = "*")
public class PresensiController {

    @Autowired
    private PresensiService presensiService;

    @PostMapping
    public ResponseEntity<ApiResponse<Presensi>> submitPresensi(@Valid @RequestBody PresensiRequest request) {
        try {
            Presensi presensi = presensiService.submitPresensi(request);
            return ResponseEntity.ok(ApiResponse.success("Presensi berhasil dicatat!", presensi));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Presensi>>> getPresensiUser(@PathVariable Integer userId) {
        try {
            List<Presensi> list = presensiService.getPresensiUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Riwayat presensi berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Presensi>>> getAllPresensi() {
        try {
            List<Presensi> list = presensiService.getAllPresensi();
            return ResponseEntity.ok(ApiResponse.success("Semua data presensi berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
