package com.carrack.track.service;

import com.carrack.track.dto.MaintenanceReminderForm;
import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.entity.Vehicle;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceReminderService {

    Page<MaintenanceReminder> searchReminders(String keyword, String status, Pageable pageable);

    MaintenanceReminder getRequiredReminder(Long id);

    MaintenanceReminder createReminder(MaintenanceReminderForm form);

    MaintenanceReminder updateReminder(Long id, MaintenanceReminderForm form);

    void markCompleted(Long id);

    List<Vehicle> listSelectableVehicles();

    long countOverdueReminders();
}
