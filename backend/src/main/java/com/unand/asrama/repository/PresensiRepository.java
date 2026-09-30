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
    Optional<Presensi> findByUserIdAndTanggalAndKeteranganContaining(Integer userId, LocalDate tanggal, String jenisPresensi);
    List<Presensi> findByUserIdAndTanggalBetween(Integer userId, LocalDate startDate, LocalDate endDate);
}
