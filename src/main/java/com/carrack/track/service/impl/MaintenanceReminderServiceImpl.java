package com.carrack.track.service.impl;

import com.carrack.track.dto.MaintenanceReminderForm;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.MaintenanceStatus;
import com.carrack.track.enums.VehicleStatus;
import com.carrack.track.repository.MaintenanceReminderRepository;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.MaintenanceReminderService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MaintenanceReminderServiceImpl implements MaintenanceReminderService {

    private final MaintenanceReminderRepository reminderRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceReminderServiceImpl(MaintenanceReminderRepository reminderRepository,
                                          VehicleRepository vehicleRepository) {
        this.reminderRepository = reminderRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Page<MaintenanceReminder> searchReminders(String keyword, String status, Pageable pageable) {
        Specification<MaintenanceReminder> spec = Specification.where(null);

        if (StringUtils.hasText(keyword)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("vehicle").get("vehicleNumber")), pattern),
                    cb.like(cb.lower(root.get("vehicle").get("brand")), pattern),
                    cb.like(cb.lower(root.get("vehicle").get("model")), pattern)
            ));
        }

        if (StringUtils.hasText(status)) {
            MaintenanceStatus parsed;
            try {
                parsed = MaintenanceStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid maintenance reminder status.");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), parsed));
        }

        return reminderRepository.findAll(spec, pageable);
    }

    @Override
    public MaintenanceReminder getRequiredReminder(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance reminder not found."));
    }

    @Override
    public MaintenanceReminder createReminder(MaintenanceReminderForm form) {
        MaintenanceReminder reminder = new MaintenanceReminder();
        applyForm(reminder, form);
        return reminderRepository.save(reminder);
    }

    @Override
    public MaintenanceReminder updateReminder(Long id, MaintenanceReminderForm form) {
        MaintenanceReminder reminder = getRequiredReminder(id);
        applyForm(reminder, form);
        return reminderRepository.save(reminder);
    }

    @Override
    public void markCompleted(Long id) {
        MaintenanceReminder reminder = getRequiredReminder(id);
        reminder.setStatus(MaintenanceStatus.COMPLETED);
        reminderRepository.save(reminder);
    }

    @Override
    public List<Vehicle> listSelectableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.getStatus() != VehicleStatus.DELETED)
                .sorted((first, second) -> first.getVehicleNumber().compareToIgnoreCase(second.getVehicleNumber()))
                .toList();
    }

    @Override
    public long countOverdueReminders() {
        return reminderRepository.countByStatusAndDueDateBefore(MaintenanceStatus.UPCOMING, LocalDate.now());
    }

    private void applyForm(MaintenanceReminder reminder, MaintenanceReminderForm form) {
        Vehicle vehicle = vehicleRepository.findById(form.getVehicleId())
                .filter(candidate -> candidate.getStatus() != VehicleStatus.DELETED)
                .orElseThrow(() -> new IllegalArgumentException("Selected vehicle is not available."));

        LocalDate dueDate = resolveDueDate(form);
        reminder.setTitle(form.getTitle().trim());
        reminder.setVehicle(vehicle);
        reminder.setLastServiceDate(form.getLastServiceDate());
        reminder.setIntervalDays(form.getIntervalDays());
        reminder.setDueDate(dueDate);
        reminder.setStatus(form.getStatus());
        reminder.setPriority(form.getPriority());
        reminder.setNote(StringUtils.hasText(form.getNote()) ? form.getNote().trim() : null);
    }

    private LocalDate resolveDueDate(MaintenanceReminderForm form) {
        if (form.getDueDate() != null) {
            return form.getDueDate();
        }
        if (form.getLastServiceDate() != null && form.getIntervalDays() != null) {
            return form.getLastServiceDate().plusDays(form.getIntervalDays());
        }
        throw new IllegalArgumentException("Enter a due date or provide last service date with interval days.");
    }
}
