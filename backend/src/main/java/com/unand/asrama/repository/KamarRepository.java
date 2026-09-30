package com.unand.asrama.repository;

import com.unand.asrama.entity.Kamar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KamarRepository extends JpaRepository<Kamar, Integer> {
    List<Kamar> findByGedungId(Integer gedungId);
    Optional<Kamar> findByNoKamarAndGedungId(String noKamar, Integer gedungId);
}
