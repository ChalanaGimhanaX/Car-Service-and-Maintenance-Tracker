package com.carrack.track.service;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.enums.ServiceStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRecordService {

    Page<ServiceRecord> searchServiceRecords(String keyword, Long vehicleId, AppUser currentUser, Pageable pageable);

    ServiceRecord getRequiredServiceRecord(Long id);

    ServiceRecord getRequiredServiceRecord(Long id, AppUser currentUser);

    ServiceRecord createServiceRecord(ServiceForm form, String actorEmail);

    ServiceRecord updateServiceRecord(Long id, ServiceForm form, String actorEmail);

    ServiceRecord updateServiceStatus(Long id, ServiceStatus status, String actorEmail);

    void deleteServiceRecord(Long id, String actorEmail);

    List<ServiceRecord> getServiceHistoryForVehicle(Long vehicleId);
}
