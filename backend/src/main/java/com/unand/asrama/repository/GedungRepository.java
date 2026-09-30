package com.unand.asrama.repository;

import com.unand.asrama.entity.Gedung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GedungRepository extends JpaRepository<Gedung, Integer> {
    Optional<Gedung> findByNama(String nama);
}
