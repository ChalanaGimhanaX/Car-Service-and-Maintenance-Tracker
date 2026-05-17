package com.carrack.track.service.impl;

import com.carrack.track.dto.MaintenanceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.ReminderStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.repository.MaintenanceReminderRepository;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.MaintenanceReminderService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MaintenanceReminderServiceImpl implements MaintenanceReminderService {

    private final MaintenanceReminderRepository maintenanceReminderRepository;
    private final VehicleRepository vehicleRepository;
    private final AuditService auditService;

    public MaintenanceReminderServiceImpl(MaintenanceReminderRepository maintenanceReminderRepository,
                                          VehicleRepository vehicleRepository,
                                          AuditService auditService) {
        this.maintenanceReminderRepository = maintenanceReminderRepository;
        this.vehicleRepository = vehicleRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<MaintenanceReminder> searchReminders(String keyword, String status, AppUser currentUser, Pageable pageable) {
        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        ReminderStatus parsedStatus = null;
        if (StringUtils.hasText(status)) {
            try {
                parsedStatus = ReminderStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid reminder status.");
            }
        }
        if (!isAdmin(currentUser)) {
            List<String> vehicleNumbers = vehicleNumbersFor(currentUser);
            if (vehicleNumbers.isEmpty()) {
                return Page.empty(pageable);
            }
            return maintenanceReminderRepository.searchRemindersForVehicles(vehicleNumbers, cleanedKeyword, parsedStatus, pageable);
        }
        return maintenanceReminderRepository.searchReminders(cleanedKeyword, parsedStatus, pageable);
    }

    @Override
    public MaintenanceReminder getRequiredReminder(Long id) {
        MaintenanceReminder reminder = maintenanceReminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found."));
        if (reminder.getDeletedAt() != null) {
            throw new IllegalArgumentException("Reminder not found.");
        }
        return reminder;
    }

    @Override
    public MaintenanceReminder getRequiredReminder(Long id, AppUser currentUser) {
        MaintenanceReminder reminder = getRequiredReminder(id);
        if (!isAdmin(currentUser) && !vehicleNumbersFor(currentUser).contains(reminder.getVehicleNumber())) {
            throw new IllegalArgumentException("Reminder not found.");
        }
        return reminder;
    }

    @Override
    public MaintenanceReminder createReminder(MaintenanceForm form, String actorEmail) {
        MaintenanceReminder reminder = new MaintenanceReminder();
        applyForm(reminder, form);
        reminder.setVehicleNumber(normalizeVehicleNumber(form.getVehicleNumber()));
        MaintenanceReminder saved = maintenanceReminderRepository.save(reminder);
        auditService.log(AuditAction.REMINDER_CREATED, actorEmail, saved.getVehicleNumber(), "Maintenance reminder created.");
        return saved;
    }

    @Override
    public MaintenanceReminder updateReminder(Long id, MaintenanceForm form, String actorEmail) {
        MaintenanceReminder reminder = getRequiredReminder(id);
        applyForm(reminder, form);
        reminder.setVehicleNumber(normalizeVehicleNumber(form.getVehicleNumber()));
        MaintenanceReminder saved = maintenanceReminderRepository.save(reminder);
        auditService.log(AuditAction.REMINDER_UPDATED, actorEmail, saved.getVehicleNumber(), "Maintenance reminder updated.");
        return saved;
    }

    @Override
    public void deleteReminder(Long id, String actorEmail) {
        MaintenanceReminder reminder = getRequiredReminder(id);
        reminder.setDeletedAt(LocalDateTime.now());
        maintenanceReminderRepository.save(reminder);
        auditService.log(AuditAction.REMINDER_DELETED, actorEmail, reminder.getVehicleNumber(), "Maintenance reminder archived.");
    }

    private void applyForm(MaintenanceReminder reminder, MaintenanceForm form) {
        reminder.setTitle(form.getTitle().trim());
        reminder.setReminderDate(form.getReminderDate());
        reminder.setLastServiceDate(form.getLastServiceDate());
        reminder.setMileageInterval(form.getMileageInterval());
        reminder.setStatus(form.getStatus());
        reminder.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
        if (form.getStatus() == ReminderStatus.COMPLETED) {
            reminder.setCompletedAt(LocalDateTime.now());
        } else {
            reminder.setCompletedAt(null);
        }
    }

    private String normalizeVehicleNumber(String vehicleNumber) {
        return vehicleNumber.trim().replaceAll("\\s+", " ").toUpperCase();
    }

    private List<String> vehicleNumbersFor(AppUser user) {
        return vehicleRepository.findByOwnerIdAndDeletedAtIsNullOrderByVehicleNumberAsc(user.getId()).stream()
                .map(Vehicle::getVehicleNumber)
                .toList();
    }

    private boolean isAdmin(AppUser user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
