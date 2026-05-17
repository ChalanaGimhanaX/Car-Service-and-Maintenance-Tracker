package com.carrack.track.service;

import com.carrack.track.dto.MaintenanceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.MaintenanceReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceReminderService {

    Page<MaintenanceReminder> searchReminders(String keyword, String status, AppUser currentUser, Pageable pageable);

    MaintenanceReminder getRequiredReminder(Long id);

    MaintenanceReminder getRequiredReminder(Long id, AppUser currentUser);

    MaintenanceReminder createReminder(MaintenanceForm form, String actorEmail);

    MaintenanceReminder updateReminder(Long id, MaintenanceForm form, String actorEmail);

    void deleteReminder(Long id, String actorEmail);
}
