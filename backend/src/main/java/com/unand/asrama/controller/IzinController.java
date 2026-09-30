package com.unand.asrama.controller;

import com.unand.asrama.dto.ApiResponse;
import com.unand.asrama.dto.IzinRequest;
import com.unand.asrama.entity.Izin;
import com.unand.asrama.service.IzinService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/izin")
@CrossOrigin(origins = "*")
public class IzinController {

    @Autowired
    private IzinService izinService;

    @PostMapping
    public ResponseEntity<ApiResponse<Izin>> submitIzin(@Valid @RequestBody IzinRequest request) {
        try {
            Izin izin = izinService.submitIzin(request, null);
            return ResponseEntity.ok(ApiResponse.success("Pengajuan izin berhasil dikirim!", izin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Izin>>> getIzinUser(@PathVariable Integer userId) {
        try {
            List<Izin> list = izinService.getIzinUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Daftar izin berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Izin>>> getAllIzin() {
        try {
            List<Izin> list = izinService.getAllIzin();
            return ResponseEntity.ok(ApiResponse.success("Semua pengajuan izin berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Izin>> updateStatus(@PathVariable Integer id, @RequestParam Izin.StatusIzin status) {
        try {
            Izin updated = izinService.updateStatusIzin(id, status);
            return ResponseEntity.ok(ApiResponse.success("Status izin berhasil diperbarui!", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
