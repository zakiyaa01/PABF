package com.unand.asrama.repository;

import com.unand.asrama.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByPresensiIdOrderByTimestampDesc(Integer presensiId);
    List<AuditLog> findAllByOrderByTimestampDesc();
}
