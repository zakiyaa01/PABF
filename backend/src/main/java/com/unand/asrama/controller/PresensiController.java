package com.unand.asrama.controller;

import com.unand.asrama.dto.ApiResponse;
import com.unand.asrama.dto.KoreksiPresensiRequest;
import com.unand.asrama.dto.PresensiRequest;
import com.unand.asrama.entity.AuditLog;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.service.MasterDataService;
import com.unand.asrama.service.PresensiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/presensi")
@CrossOrigin(origins = "*")
public class PresensiController {

    @Autowired
    private PresensiService presensiService;

    @Autowired
    private MasterDataService masterDataService;

    @PostMapping
    public ResponseEntity<ApiResponse<Presensi>> submitPresensi(
            @Valid @RequestBody PresensiRequest request,
            @RequestParam(name = "bypassTimeGate", defaultValue = "false") boolean bypassTimeGate
    ) {
        try {
            Presensi presensi = presensiService.submitPresensi(request, bypassTimeGate);
            return ResponseEntity.ok(ApiResponse.success(
                    "Presensi " + presensi.getJenisPresensi() + " berhasil dicatat! Status: " + presensi.getStatus(),
                    presensi
            ));
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(ApiResponse.error(se.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/status-hari-ini/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatusHariIni(@PathVariable Integer userId) {
        try {
            Map<String, Object> status = presensiService.getStatusPresensiHariIni(userId);
            return ResponseEntity.ok(ApiResponse.success("Status presensi hari ini berhasil diambil", status));
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

    @GetMapping("/fasil/binaan/{fasilId}")
    public ResponseEntity<ApiResponse<List<Presensi>>> getPresensiFasilBinaan(
            @PathVariable Integer fasilId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal
    ) {
        try {
            List<Presensi> list = presensiService.getPresensiFasilBinaan(fasilId, tanggal);
            return ResponseEntity.ok(ApiResponse.success("Presensi penghuni binaan berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/koreksi")
    public ResponseEntity<ApiResponse<Presensi>> koreksiPresensi(
            @PathVariable Integer id,
            @Valid @RequestBody KoreksiPresensiRequest request,
            @RequestParam(required = false) Integer fasilId,
            @RequestParam(required = false, defaultValue = "Fasilitator") String fasilNama,
            HttpServletRequest servletRequest
    ) {
        try {
            String ipAddress = servletRequest.getRemoteAddr();
            Presensi updated = presensiService.koreksiPresensi(id, request, fasilId, fasilNama, ipAddress);
            return ResponseEntity.ok(ApiResponse.success("Koreksi presensi berhasil dicatat ke Audit Trail Log!", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs(@PathVariable Integer id) {
        try {
            List<AuditLog> logs = presensiService.getAuditLogsForPresensi(id);
            return ResponseEntity.ok(ApiResponse.success("Audit log presensi berhasil diambil", logs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAllAuditLogs() {
        try {
            List<AuditLog> logs = presensiService.getAllAuditLogs();
            return ResponseEntity.ok(ApiResponse.success("Seluruh audit log berhasil diambil", logs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportPresensiExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        try {
            byte[] excelBytes = masterDataService.exportPresensiToExcel(start, end);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Rekap_Presensi_Asrama_UNAND.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Presensi>>> getAllPresensi(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tanggal
    ) {
        try {
            List<Presensi> list;
            if (tanggal != null) {
                list = presensiService.getPresensiByDate(tanggal);
            } else {
                list = presensiService.getAllPresensi();
            }
            return ResponseEntity.ok(ApiResponse.success("Data presensi berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
