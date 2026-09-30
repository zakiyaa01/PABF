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
            return ResponseEntity.ok(ApiResponse.success("Login berhasil!", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<ApiResponse<LoginResponse>> getProfile(@PathVariable Integer id) {
        try {
            User user = authService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("Data pengguna berhasil diambil", new LoginResponse(user)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
