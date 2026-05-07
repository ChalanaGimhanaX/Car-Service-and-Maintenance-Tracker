package com.carrack.track.repository;

import com.carrack.track.entity.AuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findTop10ByOrderByCreatedAtDesc();
}
