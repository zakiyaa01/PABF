package com.unand.asrama.controller;

import com.unand.asrama.dto.ApiResponse;
import com.unand.asrama.dto.UserManageRequest;
import com.unand.asrama.entity.Gedung;
import com.unand.asrama.entity.Kamar;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.GedungRepository;
import com.unand.asrama.repository.KamarRepository;
import com.unand.asrama.service.MasterDataService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class MasterDataController {

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private GedungRepository gedungRepository;

    @Autowired
    private KamarRepository kamarRepository;

    // --- User Master CRUD ---

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(@RequestParam(required = false) User.Role role) {
        try {
            List<User> list = masterDataService.getAllUsers(role);
            return ResponseEntity.ok(ApiResponse.success("Daftar pengguna berhasil diambil", list));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Integer id) {
        try {
            User user = masterDataService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("Data pengguna berhasil diambil", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserManageRequest request) {
        try {
            User user = masterDataService.createUser(request);
            return ResponseEntity.ok(ApiResponse.success("Pengguna baru berhasil ditambahkan!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Integer id, @RequestBody UserManageRequest request) {
        try {
            User user = masterDataService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success("Data pengguna berhasil diperbarui!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/users/{id}/toggle-active")
    public ResponseEntity<ApiResponse<User>> toggleActive(@PathVariable Integer id) {
        try {
            User user = masterDataService.toggleActive(id);
            String status = Boolean.TRUE.equals(user.getIsActive()) ? "diaktifkan" : "dinonaktifkan";
            return ResponseEntity.ok(ApiResponse.success("Akun berhasil " + status + "!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            String newPassword = payload.get("newPassword");
            masterDataService.resetPassword(id, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Password pengguna berhasil direset!", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- Dynamic Mapping & Room Allocation ---

    @PostMapping("/fasil/assign-lorong")
    public ResponseEntity<ApiResponse<String>> assignFasilToLorong(@RequestBody Map<String, Object> payload) {
        try {
            Integer fasilId = (Integer) payload.get("fasilId");
            String lorong = (String) payload.get("lorong");
            masterDataService.assignFasilToLorong(fasilId, lorong);
            return ResponseEntity.ok(ApiResponse.success("Fasilitator berhasil dialokasikan ke " + lorong + "!", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- System Analytics ---

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemAnalytics() {
        try {
            Map<String, Object> stats = masterDataService.getSystemAnalytics();
            return ResponseEntity.ok(ApiResponse.success("Data analitik sistem berhasil diambil", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- Excel Mass Import & Export ---

    @GetMapping("/penghuni/export-excel")
    public ResponseEntity<byte[]> exportPenghuniExcel() {
        try {
            byte[] excelBytes = masterDataService.exportPenghuniToExcel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Master_Penghuni_Asrama_UNAND.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/penghuni/import-excel")
    public ResponseEntity<ApiResponse<String>> importPenghuniExcel(@RequestParam("file") MultipartFile file) {
        try {
            int imported = masterDataService.importPenghuniFromExcel(file.getInputStream());
            return ResponseEntity.ok(ApiResponse.success("Berhasil mengimpor " + imported + " data mahasiswa dari Excel!", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Gagal impor Excel: " + e.getMessage()));
        }
    }

    // --- Gedung & Kamar Endpoints ---

    @GetMapping("/gedung")
    public ResponseEntity<ApiResponse<List<Gedung>>> getAllGedung() {
        return ResponseEntity.ok(ApiResponse.success("Data gedung berhasil diambil", gedungRepository.findAll()));
    }

    @PostMapping("/gedung")
    public ResponseEntity<ApiResponse<Gedung>> createGedung(@RequestBody Gedung gedung) {
        Gedung saved = gedungRepository.save(gedung);
        return ResponseEntity.ok(ApiResponse.success("Gedung berhasil ditambahkan", saved));
    }

    @GetMapping("/kamar")
    public ResponseEntity<ApiResponse<List<Kamar>>> getAllKamar() {
        return ResponseEntity.ok(ApiResponse.success("Data kamar berhasil diambil", kamarRepository.findAll()));
    }

    @PostMapping("/kamar")
    public ResponseEntity<ApiResponse<Kamar>> createKamar(@RequestBody Kamar kamar) {
        Kamar saved = kamarRepository.save(kamar);
        return ResponseEntity.ok(ApiResponse.success("Kamar berhasil ditambahkan", saved));
    }
}
