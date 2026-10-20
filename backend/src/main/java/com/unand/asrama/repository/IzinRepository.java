package com.unand.asrama.repository;

import com.unand.asrama.entity.Izin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IzinRepository extends JpaRepository<Izin, Integer> {
    List<Izin> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Izin> findByStatusOrderByCreatedAtDesc(Izin.StatusIzin status);
    List<Izin> findAllByOrderByCreatedAtDesc();
    List<Izin> findByUserFasilIdOrderByCreatedAtDesc(Integer fasilId);
    List<Izin> findByUserFasilIdAndStatusOrderByCreatedAtDesc(Integer fasilId, Izin.StatusIzin status);
    long countByStatus(Izin.StatusIzin status);
    List<Izin> findByUserIdAndStatusAndTglMulaiLessThanEqualAndTglSelesaiGreaterThanEqual(
            Integer userId, Izin.StatusIzin status, LocalDate date1, LocalDate date2
    );
}
