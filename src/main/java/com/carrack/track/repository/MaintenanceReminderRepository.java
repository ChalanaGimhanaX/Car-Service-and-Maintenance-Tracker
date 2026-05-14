package com.carrack.track.repository;

import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.enums.MaintenanceStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Long>, JpaSpecificationExecutor<MaintenanceReminder> {

    long countByStatusAndDueDateBefore(MaintenanceStatus status, LocalDate date);

    List<MaintenanceReminder> findTop5ByStatusOrderByDueDateAsc(MaintenanceStatus status);
}
