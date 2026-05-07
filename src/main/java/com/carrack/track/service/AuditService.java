package com.carrack.track.service;

import com.carrack.track.enums.AuditAction;
import com.carrack.track.entity.AuditLog;
import java.util.List;

public interface AuditService {

    AuditLog log(AuditAction action, String actorEmail, String targetEmail, String details);

    List<AuditLog> recentLogs();
}
