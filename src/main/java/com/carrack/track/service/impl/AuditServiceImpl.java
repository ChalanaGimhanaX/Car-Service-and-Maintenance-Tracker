package com.carrack.track.service.impl;

import com.carrack.track.entity.AuditLog;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.repository.AuditLogRepository;
import com.carrack.track.service.AuditService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog log(AuditAction action, String actorEmail, String targetEmail, String details) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setActorEmail(actorEmail);
        log.setTargetEmail(targetEmail);
        log.setDetails(details);
        return auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> recentLogs() {
        return auditLogRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
