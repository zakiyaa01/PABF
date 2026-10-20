package com.unand.asrama.service;

import com.unand.asrama.entity.Izin;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.IzinRepository;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(AttendanceScheduler.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PresensiRepository presensiRepository;

    @Autowired
    private IzinRepository izinRepository;

    // Daily Cron: 06:16 WIB (After Subuh grace period closes)
    @Scheduled(cron = "0 16 6 * * ?", zone = "Asia/Jakarta")
    @Transactional
    public void autoMarkAlphaSubuh() {
        log.info("⏰ [BATCH SCHEDULER] Running Auto-Mark Alpha for Presensi SUBUH...");
        processAutoAlpha(Presensi.JenisPresensi.SUBUH);
    }

    // Daily Cron: 20:46 WIB (After Malam grace period closes)
    @Scheduled(cron = "0 46 20 * * ?", zone = "Asia/Jakarta")
    @Transactional
    public void autoMarkAlphaMalam() {
        log.info("⏰ [BATCH SCHEDULER] Running Auto-Mark Alpha for Presensi MALAM...");
        processAutoAlpha(Presensi.JenisPresensi.MALAM);
    }

    public void processAutoAlpha(Presensi.JenisPresensi jenis) {
        LocalDate today = LocalDate.now();
        List<User> penghuniList = userRepository.findByRoleAndIsActiveTrue(User.Role.Penghuni);

        int markedCount = 0;
        for (User user : penghuniList) {
            // Check if already presensi
            Optional<Presensi> presensiOpt = presensiRepository.findByUserIdAndTanggalAndJenisPresensi(user.getId(), today, jenis);
            if (presensiOpt.isPresent()) {
                continue;
            }

            // Check if user has approved leave today
            List<Izin> approvedLeaves = izinRepository.findByUserIdAndStatusAndTglMulaiLessThanEqualAndTglSelesaiGreaterThanEqual(
                    user.getId(), Izin.StatusIzin.Disetujui, today, today
            );

            Presensi autoRecord = new Presensi();
            autoRecord.setUser(user);
            autoRecord.setTanggal(today);
            autoRecord.setWaktuAbsen(LocalDateTime.now());
            autoRecord.setJenisPresensi(jenis);

            if (!approvedLeaves.isEmpty()) {
                autoRecord.setStatus(Presensi.StatusPresensi.Izin);
                autoRecord.setKeterangan("Otomatis Izin (Surat Izin Disetujui)");
            } else {
                autoRecord.setStatus(Presensi.StatusPresensi.Alpha);
                autoRecord.setKeterangan("Otomatis Alpha (Tidak hadir hingga batas waktu)");
                markedCount++;
            }

            presensiRepository.save(autoRecord);
        }

        log.info("✅ [BATCH SCHEDULER] Completed for {}: Marked {} residents as Alpha.", jenis, markedCount);
    }
}
