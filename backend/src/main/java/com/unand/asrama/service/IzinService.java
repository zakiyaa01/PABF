package com.unand.asrama.service;

import com.unand.asrama.dto.IzinRequest;
import com.unand.asrama.dto.KeputusanIzinRequest;
import com.unand.asrama.entity.Izin;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.IzinRepository;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IzinService {

    @Autowired
    private IzinRepository izinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresensiRepository presensiRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${app.upload.dir:uploads}")
    private String uploadBaseDir;

    @Transactional
    public Izin submitIzin(IzinRequest request, String directFileBuktiPath) {
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }
        if (user == null && request.getNim() != null && !request.getNim().trim().isEmpty()) {
            user = userRepository.findByNim(request.getNim().trim()).orElse(null);
        }
        if (user == null) {
            throw new RuntimeException("Penghuni tidak ditemukan!");
        }

        if (request.getTglSelesai().isBefore(request.getTglMulai())) {
            throw new RuntimeException("Tanggal selesai tidak boleh sebelum tanggal mulai!");
        }

        String savedFilePath = directFileBuktiPath;
        if (savedFilePath == null && request.getFileBuktiBase64() != null && !request.getFileBuktiBase64().trim().isEmpty()) {
            savedFilePath = saveBase64Bukti(request.getFileBuktiBase64(), user.getNim());
        }

        Izin izin = new Izin();
        izin.setUser(user);
        izin.setTglMulai(request.getTglMulai());
        izin.setTglSelesai(request.getTglSelesai());
        izin.setKategori(request.getKategori() != null ? request.getKategori() : Izin.KategoriIzin.Lainnya);
        izin.setAlasan(request.getAlasan());
        izin.setFileBukti(savedFilePath);
        izin.setStatus(Izin.StatusIzin.Menunggu);
        izin.setCreatedAt(LocalDateTime.now());

        Izin saved = izinRepository.save(izin);

        // Notify fasil
        if (user.getFasil() != null) {
            notificationService.sendPushNotification(
                    user.getFasil().getEmail(),
                    "Pengajuan Izin Baru",
                    "Penghuni binaan " + user.getNama() + " (" + user.getNim() + ") mengajukan izin baru (" + izin.getKategori() + ")."
            );
        }

        return saved;
    }

    private String saveBase64Bukti(String base64Content, String nim) {
        try {
            String clean = base64Content;
            if (clean.contains(",")) {
                clean = clean.substring(clean.indexOf(",") + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(clean.trim());

            File dir = new File(uploadBaseDir + "/izin");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = "izin_" + (nim != null ? nim : "dokumen") + "_" + System.currentTimeMillis() + ".jpg";
            File file = new File(dir, filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
            return "/uploads/izin/" + filename;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Izin> getIzinUser(Integer userId) {
        return izinRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Izin> getAllIzin() {
        return izinRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Izin> getIzinFasilBinaan(Integer fasilId, Izin.StatusIzin status) {
        if (status != null) {
            return izinRepository.findByUserFasilIdAndStatusOrderByCreatedAtDesc(fasilId, status);
        }
        return izinRepository.findByUserFasilIdOrderByCreatedAtDesc(fasilId);
    }

    @Transactional
    public Izin reviewIzin(Integer izinId, KeputusanIzinRequest request, Integer reviewerId) {
        Izin izin = izinRepository.findById(izinId)
                .orElseThrow(() -> new RuntimeException("Data izin ID " + izinId + " tidak ditemukan!"));

        User reviewer = reviewerId != null ? userRepository.findById(reviewerId).orElse(null) : null;

        izin.setStatus(request.getStatus());
        izin.setCatatanFasil(request.getCatatanFasil());
        izin.setFasil(reviewer);
        izin.setReviewedAt(LocalDateTime.now());

        Izin saved = izinRepository.save(izin);

        // If approved, create or update presensi status as Izin for today if within date range
        if (request.getStatus() == Izin.StatusIzin.Disetujui) {
            LocalDate today = LocalDate.now();
            if (!today.isBefore(izin.getTglMulai()) && !today.isAfter(izin.getTglSelesai())) {
                markPresensiAsIzin(izin.getUser(), today, izin.getAlasan());
            }
        }

        // Send Push Notification via FCM Mock to resident
        notificationService.sendPushNotification(
                izin.getUser().getNim() != null ? izin.getUser().getNim() : izin.getUser().getEmail(),
                "Status Pengajuan Izin Diperbarui",
                "Pengajuan izin Anda telah " + request.getStatus().name().toUpperCase() +
                        " oleh Fasil. Catatan: " + request.getCatatanFasil()
        );

        return saved;
    }

    private void markPresensiAsIzin(User user, LocalDate date, String alasan) {
        for (Presensi.JenisPresensi jenis : Presensi.JenisPresensi.values()) {
            Optional<Presensi> existing = presensiRepository.findByUserIdAndTanggalAndJenisPresensi(user.getId(), date, jenis);
            if (existing.isEmpty()) {
                Presensi p = new Presensi();
                p.setUser(user);
                p.setTanggal(date);
                p.setWaktuAbsen(LocalDateTime.now());
                p.setJenisPresensi(jenis);
                p.setStatus(Presensi.StatusPresensi.Izin);
                p.setKeterangan("Izin Disetujui: " + alasan);
                presensiRepository.save(p);
            }
        }
    }
}
