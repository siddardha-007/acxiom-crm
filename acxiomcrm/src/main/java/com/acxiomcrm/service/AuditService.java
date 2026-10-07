package com.acxiomcrm.service;

import com.acxiomcrm.entity.AppUser;
import com.acxiomcrm.entity.AuditLog;
import com.acxiomcrm.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            AppUser user,
            String action,
            String entityName,
            Long recordId,
            String oldValue,
            String newValue,
            String ipAddress
    ) {

        AuditLog auditLog =
                new AuditLog(
                        user,
                        action,
                        entityName,
                        recordId,
                        oldValue,
                        newValue,
                        ipAddress
                );

        auditLogRepository.save(auditLog);
    }
}