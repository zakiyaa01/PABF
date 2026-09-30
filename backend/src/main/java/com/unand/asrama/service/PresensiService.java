package com.unand.asrama.service;

import com.unand.asrama.dto.PresensiRequest;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PresensiService {

    @Autowired
    private PresensiRepository presensiRepository;

    @Autowired
    private UserRepository userRepository;

    public Presensi submitPresensi(PresensiRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        String jenis = request.getJenisPresensi() != null ? request.getJenisPresensi() : "Presensi Harian";

        // Cek pencegahan presensi ganda untuk hari & jenis yang sama
        Optional<Presensi> existing = presensiRepository.findByUserIdAndTanggalAndKeteranganContaining(
                user.getId(), today, jenis
        );

        if (existing.isPresent()) {
            throw new RuntimeException("Anda sudah melakukan " + jenis + " hari ini (" + today + ")!");
        }

        Presensi presensi = new Presensi();
        presensi.setUser(user);
        presensi.setTanggal(today);
        presensi.setWaktuAbsen(now);
        presensi.setStatus(Presensi.StatusPresensi.Hadir);
        presensi.setLatitude(request.getLatitude());
        presensi.setLongitude(request.getLongitude());
        presensi.setKeterangan(jenis + (request.getKeterangan() != null ? " - " + request.getKeterangan() : ""));

        return presensiRepository.save(presensi);
    }

    public List<Presensi> getPresensiUser(Integer userId) {
        return presensiRepository.findByUserIdOrderByWaktuAbsenDesc(userId);
    }

    public List<Presensi> getAllPresensi() {
        return presensiRepository.findAll();
    }
}
