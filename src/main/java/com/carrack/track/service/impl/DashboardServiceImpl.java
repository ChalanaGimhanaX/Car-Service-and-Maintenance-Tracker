package com.carrack.track.service.impl;

import com.carrack.track.dto.DashboardSnapshot;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.repository.MaintenanceReminderRepository;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.DashboardService;
import java.time.LocalDateTime;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final MaintenanceReminderRepository maintenanceReminderRepository;
    private final AuditService auditService;

    public DashboardServiceImpl(AppUserRepository userRepository,
                                VehicleRepository vehicleRepository,
                                ServiceRecordRepository serviceRecordRepository,
                                MaintenanceReminderRepository maintenanceReminderRepository,
                                AuditService auditService) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRecordRepository = serviceRecordRepository;
        this.maintenanceReminderRepository = maintenanceReminderRepository;
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
                vehicleRepository.countByDeletedAtIsNull(),
                serviceRecordRepository.count(),
                maintenanceReminderRepository.countByDeletedAtIsNull(),
                userRepository.findTop5ByStatusNotOrderByCreatedAtDesc(AccountStatus.DELETED),
                auditService.recentLogs()
        );
    }
}
