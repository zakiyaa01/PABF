package com.unand.asrama.service;

import com.unand.asrama.dto.KoreksiPresensiRequest;
import com.unand.asrama.dto.PresensiRequest;
import com.unand.asrama.entity.AuditLog;
import com.unand.asrama.entity.Gedung;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.AuditLogRepository;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class PresensiService {

    @Autowired
    private PresensiRepository presensiRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private WatermarkService watermarkService;

    @Value("${app.presensi.enforce-time-window:true}")
    private boolean enforceTimeWindow;

    @Value("${app.presensi.enforce-geofence:true}")
    private boolean enforceGeofence;

    @Value("${app.presensi.enforce-device-binding:true}")
    private boolean enforceDeviceBinding;

    // Time window definitions (Asia/Jakarta)
    private static final LocalTime SUBUH_START = LocalTime.of(4, 0);
    private static final LocalTime SUBUH_END = LocalTime.of(6, 0);
    private static final LocalTime SUBUH_GRACE = LocalTime.of(6, 15);

    private static final LocalTime MALAM_START = LocalTime.of(18, 0);
    private static final LocalTime MALAM_END = LocalTime.of(20, 30);
    private static final LocalTime MALAM_GRACE = LocalTime.of(20, 45);

    @Transactional
    public Presensi submitPresensi(PresensiRequest request, boolean bypassTimeGate) {
        // 1. Identify User
        User user = null;
        if (request.getNim() != null && !request.getNim().trim().isEmpty()) {
            user = userRepository.findByNim(request.getNim().trim()).orElse(null);
        }
        if (user == null && request.getUserId() != null) {
            user = userRepository.findById(request.getUserId()).orElse(null);
        }
        if (user == null) {
            throw new RuntimeException("Penghuni tidak ditemukan di database!");
        }

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new RuntimeException("Akun Anda telah dinonaktifkan oleh Admin Asrama.");
        }

        // 2. Anti-Spoofing & Emulator Detection
        if (Boolean.TRUE.equals(request.getIsMockLocation())) {
            throw new SecurityException("Deteksi Pelanggaran: Fake GPS / Mock Location terdeteksi aktif! Presensi ditolak.");
        }
        if (Boolean.TRUE.equals(request.getIsEmulator())) {
            throw new SecurityException("Deteksi Lingkungan Ilegal: Terdeteksi dijalankan pada Emulator! Presensi wajib menggunakan perangkat HP fisik.");
        }

        // 3. Biometric Device Binding & Single-Device Lock
        if (request.getDeviceDeviceId() != null && !request.getDeviceDeviceId().trim().isEmpty()) {
            String incomingDeviceId = request.getDeviceDeviceId().trim();
            if (user.getDeviceId() == null || user.getDeviceId().trim().isEmpty()) {
                user.setDeviceId(incomingDeviceId);
                userRepository.save(user);
            } else if (enforceDeviceBinding && user.getRole() == User.Role.Penghuni && !user.getDeviceId().equalsIgnoreCase(incomingDeviceId)) {
                throw new SecurityException("Single-Device Lock: Akun NIM " + user.getNim() +
                        " telah terikat pada perangkat lain (ID: " + user.getDeviceId() + "). Penitipan presensi dilarang!");
            }
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // 4. Determine Jenis Presensi & Server Time-Gated Window
        Presensi.JenisPresensi jenisPresensi;
        if (request.getJenisPresensi() != null && request.getJenisPresensi().equalsIgnoreCase("MALAM")) {
            jenisPresensi = Presensi.JenisPresensi.MALAM;
        } else {
            jenisPresensi = Presensi.JenisPresensi.SUBUH;
        }

        Presensi.StatusPresensi initialStatus = Presensi.StatusPresensi.Hadir;

        if (enforceTimeWindow && !bypassTimeGate) {
            if (jenisPresensi == Presensi.JenisPresensi.SUBUH) {
                if (currentTime.isBefore(SUBUH_START) || currentTime.isAfter(SUBUH_GRACE)) {
                    throw new RuntimeException("Presensi Subuh ditutup! Jadwal aktif: 04.00 - 06.00 WIB (Toleransi keterlambatan hingga 06.15 WIB). Waktu server saat ini: " + currentTime.toString().substring(0, 5) + " WIB");
                } else if (currentTime.isAfter(SUBUH_END)) {
                    initialStatus = Presensi.StatusPresensi.Terlambat;
                }
            } else {
                if (currentTime.isBefore(MALAM_START) || currentTime.isAfter(MALAM_GRACE)) {
                    throw new RuntimeException("Presensi Malam ditutup! Jadwal aktif: 18.00 - 20.30 WIB (Toleransi keterlambatan hingga 20.45 WIB). Waktu server saat ini: " + currentTime.toString().substring(0, 5) + " WIB");
                } else if (currentTime.isAfter(MALAM_END)) {
                    initialStatus = Presensi.StatusPresensi.Terlambat;
                }
            }
        }

        // 5. Auto-Lock & Duplicate Presence Prevention
        Optional<Presensi> existing = presensiRepository.findByUserIdAndTanggalAndJenisPresensi(
                user.getId(), today, jenisPresensi
        );
        if (existing.isPresent()) {
            throw new RuntimeException("Tombol Terkunci (Auto-Lock): Anda sudah berhasil mencatat Presensi " +
                    jenisPresensi + " untuk hari ini (" + today + ")!");
        }

        // 6. Multi-Layered Location Validation (Haversine Formula Geofencing)
        Gedung gedung = (user.getKamar() != null && user.getKamar().getGedung() != null)
                ? user.getKamar().getGedung()
                : null;

        double centerLat = gedung != null && gedung.getLatitude() != null ? gedung.getLatitude().doubleValue() : -0.914231;
        double centerLon = gedung != null && gedung.getLongitude() != null ? gedung.getLongitude().doubleValue() : 100.466012;
        double maxRadius = gedung != null && gedung.getRadiusMeter() != null ? gedung.getRadiusMeter() : 50.0;
        String namaGedung = gedung != null ? gedung.getNama() : "Asrama UNAND";

        double userLat = request.getLatitude() != null ? request.getLatitude().doubleValue() : centerLat;
        double userLon = request.getLongitude() != null ? request.getLongitude().doubleValue() : centerLon;

        double distance = calculateHaversineDistanceMeters(centerLat, centerLon, userLat, userLon);

        if (enforceGeofence && !bypassTimeGate && distance > maxRadius) {
            throw new RuntimeException(String.format(
                    "Di luar jangkauan asrama! Jarak Anda %.1f meter dari %s (Maksimum radius diizinkan: %.0f meter).",
                    distance, namaGedung, maxRadius
            ));
        }

        // 7. Dynamic Geotagging & Watermarking on Selfie Photo
        String savedPhotoPath = null;
        if (request.getPhotoBase64() != null && !request.getPhotoBase64().trim().isEmpty()) {
            savedPhotoPath = watermarkService.processAndSaveWatermarkedPhoto(
                    request.getPhotoBase64(),
                    user.getNim(),
                    user.getNama(),
                    namaGedung,
                    userLat,
                    userLon,
                    request.getAccuracyMeters(),
                    distance,
                    now
            );
        }

        // 8. Build & Save Presensi Record
        Presensi presensi = new Presensi();
        presensi.setUser(user);
        presensi.setTanggal(today);
        presensi.setWaktuAbsen(now);
        presensi.setJenisPresensi(jenisPresensi);
        presensi.setStatus(initialStatus);
        presensi.setLatitude(request.getLatitude() != null ? request.getLatitude() : BigDecimal.valueOf(userLat));
        presensi.setLongitude(request.getLongitude() != null ? request.getLongitude() : BigDecimal.valueOf(userLon));
        presensi.setAccuracyMeters(request.getAccuracyMeters() != null ? request.getAccuracyMeters() : 5.0);
        presensi.setDistanceMeters(distance);
        presensi.setIsMockLocation(request.getIsMockLocation());
        presensi.setIsEmulator(request.getIsEmulator());
        presensi.setWifiBssid(request.getWifiBssid());
        presensi.setDeviceId(request.getDeviceDeviceId() != null ? request.getDeviceDeviceId() : user.getDeviceId());
        presensi.setDeviceTimestamp(request.getDeviceTimestamp() != null ? request.getDeviceTimestamp() : now.toString());
        presensi.setFotoWajah(savedPhotoPath);
        presensi.setKeterangan(request.getKeterangan() != null ? request.getKeterangan() : (jenisPresensi + " - Sukses"));

        return presensiRepository.save(presensi);
    }

    public static double calculateHaversineDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    @Transactional
    public Presensi koreksiPresensi(Integer presensiId, KoreksiPresensiRequest request, Integer fasilId, String fasilNama, String ipAddress) {
        Presensi presensi = presensiRepository.findById(presensiId)
                .orElseThrow(() -> new RuntimeException("Data presensi ID " + presensiId + " tidak ditemukan!"));

        String statusLama = presensi.getStatus().name();
        String statusBaru = request.getStatusBaru().name();

        presensi.setStatus(request.getStatusBaru());
        presensi.setKeterangan((presensi.getKeterangan() != null ? presensi.getKeterangan() + " | " : "") +
                "[Koreksi Fasil " + fasilNama + ": " + request.getAlasan() + "]");

        Presensi updated = presensiRepository.save(presensi);

        // Immutable Audit Trail Log entry
        AuditLog auditLog = new AuditLog(
                updated,
                fasilId != null ? fasilId : 0,
                fasilNama != null ? fasilNama : "Fasilitator",
                statusLama,
                statusBaru,
                ipAddress,
                request.getAlasan()
        );
        auditLogRepository.save(auditLog);

        return updated;
    }

    public List<Presensi> getPresensiUser(Integer userId) {
        return presensiRepository.findByUserIdOrderByWaktuAbsenDesc(userId);
    }

    public List<Presensi> getAllPresensi() {
        return presensiRepository.findAll();
    }

    public List<Presensi> getPresensiByDate(LocalDate date) {
        return presensiRepository.findByTanggalOrderByWaktuAbsenDesc(date);
    }

    public List<Presensi> getPresensiByDateRange(LocalDate start, LocalDate end) {
        return presensiRepository.findByTanggalBetweenOrderByWaktuAbsenDesc(start, end);
    }

    public List<Presensi> getPresensiFasilBinaan(Integer fasilId, LocalDate tanggal) {
        if (tanggal != null) {
            return presensiRepository.findByUserFasilIdAndTanggalOrderByWaktuAbsenDesc(fasilId, tanggal);
        }
        return presensiRepository.findByUserFasilIdOrderByWaktuAbsenDesc(fasilId);
    }

    public Map<String, Object> getStatusPresensiHariIni(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Optional<Presensi> subuh = presensiRepository.findByUserIdAndTanggalAndJenisPresensi(
                userId, today, Presensi.JenisPresensi.SUBUH
        );
        Optional<Presensi> malam = presensiRepository.findByUserIdAndTanggalAndJenisPresensi(
                userId, today, Presensi.JenisPresensi.MALAM
        );

        Map<String, Object> result = new HashMap<>();
        result.put("today", today.toString());
        result.put("currentTime", now.toString().substring(0, 5));

        Map<String, Object> subuhInfo = new HashMap<>();
        subuhInfo.put("sudahPresensi", subuh.isPresent());
        subuhInfo.put("record", subuh.orElse(null));
        subuhInfo.put("isActiveWindow", !now.isBefore(SUBUH_START) && !now.isAfter(SUBUH_GRACE));
        subuhInfo.put("isGracePeriod", now.isAfter(SUBUH_END) && !now.isAfter(SUBUH_GRACE));
        subuhInfo.put("startTime", "04:00");
        subuhInfo.put("endTime", "06:00");
        subuhInfo.put("graceTime", "06:15");
        result.put("subuh", subuhInfo);

        Map<String, Object> malamInfo = new HashMap<>();
        malamInfo.put("sudahPresensi", malam.isPresent());
        malamInfo.put("record", malam.orElse(null));
        malamInfo.put("isActiveWindow", !now.isBefore(MALAM_START) && !now.isAfter(MALAM_GRACE));
        malamInfo.put("isGracePeriod", now.isAfter(MALAM_END) && !now.isAfter(MALAM_GRACE));
        malamInfo.put("startTime", "18:00");
        malamInfo.put("endTime", "20:30");
        malamInfo.put("graceTime", "20:45");
        result.put("malam", malamInfo);

        return result;
    }

    public List<AuditLog> getAuditLogsForPresensi(Integer presensiId) {
        return auditLogRepository.findByPresensiIdOrderByTimestampDesc(presensiId);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}
