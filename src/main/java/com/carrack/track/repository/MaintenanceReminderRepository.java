package com.carrack.track.repository;

import com.carrack.track.entity.MaintenanceReminder;
import com.carrack.track.enums.ReminderStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaintenanceReminderRepository extends JpaRepository<MaintenanceReminder, Long> {

    @Query("""
            select r from MaintenanceReminder r
            where r.deletedAt is null
              and (:status is null or r.status = :status)
              and (:keyword is null
                   or lower(r.vehicleNumber) like lower(concat('%', :keyword, '%'))
                   or lower(r.title) like lower(concat('%', :keyword, '%')))
            """)
    Page<MaintenanceReminder> searchReminders(@Param("keyword") String keyword,
                                              @Param("status") ReminderStatus status,
                                              Pageable pageable);

    @Query("""
            select r from MaintenanceReminder r
            where r.deletedAt is null
              and r.vehicleNumber in :vehicleNumbers
              and (:status is null or r.status = :status)
              and (:keyword is null
                   or lower(r.vehicleNumber) like lower(concat('%', :keyword, '%'))
                   or lower(r.title) like lower(concat('%', :keyword, '%')))
            """)
    Page<MaintenanceReminder> searchRemindersForVehicles(@Param("vehicleNumbers") List<String> vehicleNumbers,
                                                         @Param("keyword") String keyword,
                                                         @Param("status") ReminderStatus status,
                                                         Pageable pageable);

    long countByDeletedAtIsNull();

    long countByVehicleNumberInAndDeletedAtIsNull(List<String> vehicleNumbers);

    List<MaintenanceReminder> findTop5ByVehicleNumberInAndDeletedAtIsNullOrderByReminderDateAsc(List<String> vehicleNumbers);
}
