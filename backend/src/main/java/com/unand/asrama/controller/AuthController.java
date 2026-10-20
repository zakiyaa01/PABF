package com.unand.asrama.controller;

import com.unand.asrama.dto.ApiResponse;
import com.unand.asrama.dto.LoginRequest;
import com.unand.asrama.dto.LoginResponse;
import com.unand.asrama.entity.User;
import com.unand.asrama.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login berhasil! Selamat datang di SIA-Asrama UNAND.", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> payload) {
        try {
            String refreshToken = payload.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token wajib disertakan!"));
            }
            LoginResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(ApiResponse.success("Token berhasil diperbarui!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout berhasil! Sesi telah diakhiri.", "OK"));
    }

    @PostMapping("/bind-device")
    public ResponseEntity<ApiResponse<String>> bindDevice(@RequestBody Map<String, Object> payload) {
        try {
            Integer userId = (Integer) payload.get("userId");
            String deviceId = (String) payload.get("deviceId");
            authService.bindDevice(userId, deviceId);
            return ResponseEntity.ok(ApiResponse.success("Perangkat berhasil dikunci (Single-Device Lock)!", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/unbind-device/{userId}")
    public ResponseEntity<ApiResponse<String>> unbindDevice(@PathVariable Integer userId) {
        try {
            authService.unbindDevice(userId);
            return ResponseEntity.ok(ApiResponse.success("Kunci perangkat berhasil dilepas!", "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<ApiResponse<LoginResponse>> getProfile(@PathVariable Integer id) {
        try {
            User user = authService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("Data profil berhasil diambil", new LoginResponse(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
