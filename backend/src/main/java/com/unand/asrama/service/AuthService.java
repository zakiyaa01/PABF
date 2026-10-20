package com.unand.asrama.service;

import com.unand.asrama.dto.LoginRequest;
import com.unand.asrama.dto.LoginResponse;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.UserRepository;
import com.unand.asrama.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${app.jwt.access-expiration-ms:7200000}")
    private long jwtAccessExpirationMs;

    public LoginResponse login(LoginRequest request) {
        String identifier = request.getIdentifier() != null ? request.getIdentifier().trim() : "";
        Optional<User> userOpt = userRepository.findByEmailOrNim(identifier, identifier);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Akun dengan NIM/NIP/Email '" + identifier + "' tidak ditemukan!");
        }

        User user = userOpt.get();

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Akun Anda sedang dinonaktifkan oleh Administrator Asrama.");
        }

        // Biometric login bypasses password verification if verified by BiometricPrompt
        if (Boolean.TRUE.equals(request.getIsBiometric())) {
            if (user.getDeviceId() != null && request.getDeviceId() != null && !user.getDeviceId().equals(request.getDeviceId())) {
                throw new RuntimeException("Autentikasi Biometrik gagal! Perangkat tidak terdaftar untuk akun ini.");
            }
        } else {
            // Verify Password
            boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword()) ||
                              request.getPassword().equals(user.getPassword());

            if (!matches) {
                throw new RuntimeException("Password yang Anda masukkan salah!");
            }
        }

        // Single-Device Lock: Device Binding Logic
        if (request.getDeviceId() != null && !request.getDeviceId().trim().isEmpty()) {
            String incomingDeviceId = request.getDeviceId().trim();
            if (user.getDeviceId() == null || user.getDeviceId().trim().isEmpty()) {
                // First-time device binding
                user.setDeviceId(incomingDeviceId);
                userRepository.save(user);
            } else if (user.getRole() == User.Role.Penghuni && !user.getDeviceId().equalsIgnoreCase(incomingDeviceId)) {
                // Reject device sharing / joki
                throw new RuntimeException("Perangkat Tidak Cocok (Single-Device Lock)! Akun NIM " + user.getNim() +
                        " telah terkunci pada perangkat lain. Penitipan presensi dilarang!");
            }
        }

        // Generate JWT Tokens
        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        return new LoginResponse(user, accessToken, refreshToken, jwtAccessExpirationMs);
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token tidak valid atau telah kedaluwarsa!");
        }

        Integer userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan!"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Akun sedang dinonaktifkan!");
        }

        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        return new LoginResponse(user, newAccessToken, newRefreshToken, jwtAccessExpirationMs);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan!"));
    }

    public void bindDevice(Integer userId, String deviceId) {
        User user = getUserById(userId);
        user.setDeviceId(deviceId);
        user.setBiometricEnabled(true);
        userRepository.save(user);
    }

    public void unbindDevice(Integer userId) {
        User user = getUserById(userId);
        user.setDeviceId(null);
        user.setBiometricEnabled(false);
        userRepository.save(user);
    }
}
