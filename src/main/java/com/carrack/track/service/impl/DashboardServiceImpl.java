package com.carrack.track.service.impl;

import com.carrack.track.dto.DashboardSnapshot;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.DashboardService;
import java.time.LocalDateTime;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AppUserRepository userRepository;
    private final AuditService auditService;

    public DashboardServiceImpl(AppUserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Override
    public DashboardSnapshot snapshot() {
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        return new DashboardSnapshot(
                userRepository.countByStatusNot(AccountStatus.DELETED),
                userRepository.countByStatus(AccountStatus.ACTIVE),
                userRepository.countByRoleAndStatusNot(Role.ADMIN, AccountStatus.DELETED),
                userRepository.countByStatus(AccountStatus.SUSPENDED),
                userRepository.countByCreatedAtAfter(monthStart),
                userRepository.findTop5ByStatusNotOrderByCreatedAtDesc(AccountStatus.DELETED),
                auditService.recentLogs()
        );
    }
}
