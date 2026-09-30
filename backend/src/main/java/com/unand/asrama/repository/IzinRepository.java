package com.unand.asrama.repository;

import com.unand.asrama.entity.Izin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IzinRepository extends JpaRepository<Izin, Integer> {
    List<Izin> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Izin> findByStatusOrderByCreatedAtDesc(Izin.StatusIzin status);
    List<Izin> findAllByOrderByCreatedAtDesc();
}
