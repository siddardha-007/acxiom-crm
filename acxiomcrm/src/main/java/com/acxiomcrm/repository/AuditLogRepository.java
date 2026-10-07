package com.acxiomcrm.repository;

import com.acxiomcrm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByEntityName(
            String entityName
    );

    List<AuditLog> findByAction(
            String action
    );

    List<AuditLog>
    findByCreatedDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<AuditLog>
    findByUserIdAndCreatedDateBetween(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}