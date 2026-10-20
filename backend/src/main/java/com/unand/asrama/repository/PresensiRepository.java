package com.unand.asrama.repository;

import com.unand.asrama.entity.Presensi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresensiRepository extends JpaRepository<Presensi, Integer> {
    List<Presensi> findByUserIdOrderByWaktuAbsenDesc(Integer userId);
    List<Presensi> findByTanggalOrderByWaktuAbsenDesc(LocalDate tanggal);
    Optional<Presensi> findByUserIdAndTanggalAndJenisPresensi(Integer userId, LocalDate tanggal, Presensi.JenisPresensi jenisPresensi);
    Optional<Presensi> findByUserIdAndTanggalAndKeteranganContaining(Integer userId, LocalDate tanggal, String jenisPresensi);
    List<Presensi> findByUserIdAndTanggalBetweenOrderByWaktuAbsenDesc(Integer userId, LocalDate startDate, LocalDate endDate);
    List<Presensi> findByTanggalBetweenOrderByWaktuAbsenDesc(LocalDate startDate, LocalDate endDate);
    List<Presensi> findByUserFasilIdOrderByWaktuAbsenDesc(Integer fasilId);
    List<Presensi> findByUserFasilIdAndTanggalOrderByWaktuAbsenDesc(Integer fasilId, LocalDate tanggal);
    long countByTanggalAndStatus(LocalDate tanggal, Presensi.StatusPresensi status);
}
