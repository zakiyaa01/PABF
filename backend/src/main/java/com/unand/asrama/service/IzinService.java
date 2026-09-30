package com.unand.asrama.service;

import com.unand.asrama.dto.IzinRequest;
import com.unand.asrama.entity.Izin;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.IzinRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IzinService {

    @Autowired
    private IzinRepository izinRepository;

    @Autowired
    private UserRepository userRepository;

    public Izin submitIzin(IzinRequest request, String fileBuktiPath) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan!"));

        Izin izin = new Izin();
        izin.setUser(user);
        izin.setTglMulai(request.getTglMulai());
        izin.setTglSelesai(request.getTglSelesai());
        izin.setAlasan(request.getAlasan());
        izin.setFileBukti(fileBuktiPath);
        izin.setStatus(Izin.StatusIzin.Menunggu);
        izin.setCreatedAt(LocalDateTime.now());

        return izinRepository.save(izin);
    }

    public List<Izin> getIzinUser(Integer userId) {
        return izinRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Izin> getAllIzin() {
        return izinRepository.findAllByOrderByCreatedAtDesc();
    }

    public Izin updateStatusIzin(Integer id, Izin.StatusIzin status) {
        Izin izin = izinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data izin tidak ditemukan!"));
        izin.setStatus(status);
        return izinRepository.save(izin);
    }
}
