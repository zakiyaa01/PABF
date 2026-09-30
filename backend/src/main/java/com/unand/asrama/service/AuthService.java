package com.unand.asrama.service;

import com.unand.asrama.dto.LoginRequest;
import com.unand.asrama.dto.LoginResponse;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmailOrNim(request.getIdentifier(), request.getIdentifier());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email atau NIM tidak ditemukan!");
        }

        User user = userOpt.get();

        // Check password (BCrypt or plain text fallback for initial testing)
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword()) || 
                          request.getPassword().equals(user.getPassword());

        if (!matches) {
            throw new RuntimeException("Password yang Anda masukkan salah!");
        }

        return new LoginResponse(user);
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + id + " tidak ditemukan!"));
    }
}
