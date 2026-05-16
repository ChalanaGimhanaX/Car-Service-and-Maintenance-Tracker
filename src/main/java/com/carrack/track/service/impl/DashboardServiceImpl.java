package com.carrack.track.service.impl;

import com.carrack.track.dto.ClientDashboardSnapshot;
import com.carrack.track.dto.DashboardSnapshot;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.repository.InvoiceRepository;
import com.carrack.track.repository.MaintenanceReminderRepository;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.DashboardService;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final AppUserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final MaintenanceReminderRepository maintenanceReminderRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditService auditService;

    public DashboardServiceImpl(AppUserRepository userRepository,
                                VehicleRepository vehicleRepository,
                                ServiceRecordRepository serviceRecordRepository,
                                MaintenanceReminderRepository maintenanceReminderRepository,
                                InvoiceRepository invoiceRepository,
                                AuditService auditService) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.serviceRecordRepository = serviceRecordRepository;
        this.maintenanceReminderRepository = maintenanceReminderRepository;
        this.invoiceRepository = invoiceRepository;
        this.auditService = auditService;
    }

    @Override
    public DashboardSnapshot adminSnapshot() {
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
                invoiceRepository.count(),
                userRepository.findTop5ByStatusNotOrderByCreatedAtDesc(AccountStatus.DELETED),
                auditService.recentLogs()
        );
    }

    @Override
    public ClientDashboardSnapshot clientSnapshot(AppUser user) {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerIdAndDeletedAtIsNullOrderByVehicleNumberAsc(user.getId());
        List<String> vehicleNumbers = vehicles.stream()
                .map(Vehicle::getVehicleNumber)
                .toList();

        long reminderCount = vehicleNumbers.isEmpty()
                ? 0
                : maintenanceReminderRepository.countByVehicleNumberInAndDeletedAtIsNull(vehicleNumbers);
        List<com.carrack.track.entity.MaintenanceReminder> reminders = vehicleNumbers.isEmpty()
                ? Collections.emptyList()
                : maintenanceReminderRepository.findTop5ByVehicleNumberInAndDeletedAtIsNullOrderByReminderDateAsc(vehicleNumbers);

        return new ClientDashboardSnapshot(
                vehicleRepository.countByOwnerIdAndDeletedAtIsNull(user.getId()),
                serviceRecordRepository.countByVehicleOwnerId(user.getId()),
                reminderCount,
                invoiceRepository.countByServiceRecordVehicleOwnerId(user.getId()),
                serviceRecordRepository.findTop5ByVehicleOwnerIdOrderByServiceDateDesc(user.getId()),
                reminders,
                invoiceRepository.findTop5ByServiceRecordVehicleOwnerIdOrderByInvoiceDateDesc(user.getId())
        );
    }
}
