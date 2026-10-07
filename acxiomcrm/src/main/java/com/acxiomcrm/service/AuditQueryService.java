package com.acxiomcrm.service;

import com.acxiomcrm.dto.AuditLogResponse;
import com.acxiomcrm.entity.AuditLog;
import com.acxiomcrm.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditQueryService {

    private final AuditLogRepository auditLogRepository;

    public AuditQueryService(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogResponse> getAll() {

        return auditLogRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AuditLogResponse> filter(
            Long userId,
            String action,
            String entityName,
            LocalDateTime start,
            LocalDateTime end
    ) {

        List<AuditLog> logs =
                auditLogRepository.findAll();

        return logs.stream()
                .filter(log ->
                        userId == null ||
                                (log.getUser() != null &&
                                        log.getUser()
                                                .getId()
                                                .equals(userId))
                )
                .filter(log ->
                        action == null ||
                                action.isBlank() ||
                                log.getAction()
                                        .equalsIgnoreCase(action)
                )
                .filter(log ->
                        entityName == null ||
                                entityName.isBlank() ||
                                log.getEntityName()
                                        .equalsIgnoreCase(
                                                entityName
                                        )
                )
                .filter(log ->
                        start == null ||
                                !log.getCreatedDate()
                                        .isBefore(start)
                )
                .filter(log ->
                        end == null ||
                                !log.getCreatedDate()
                                        .isAfter(end)
                )
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(
            AuditLog log
    ) {

        return new AuditLogResponse(
                log.getId(),
                log.getUser() != null
                        ? log.getUser().getId()
                        : null,
                log.getUser() != null
                        ? log.getUser().getName()
                        : null,
                log.getAction(),
                log.getEntityName(),
                log.getRecordId(),
                log.getOldValue(),
                log.getNewValue(),
                log.getCreatedDate(),
                log.getIpAddress()
        );
    }
}