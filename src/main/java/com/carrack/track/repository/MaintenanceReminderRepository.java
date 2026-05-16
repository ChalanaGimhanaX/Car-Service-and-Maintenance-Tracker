package com.carrack.track.repository;

import com.carrack.track.entity.MaintenanceReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Long>, JpaSpecificationExecutor<MaintenanceReminder> {

    long countByDeletedAtIsNull();
}
