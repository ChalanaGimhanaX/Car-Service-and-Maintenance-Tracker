// Service interface defining operations for service record management
package com.carrack.track.service;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.ServiceRecord;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRecordService {

    Page<ServiceRecord> searchServiceRecords(String keyword, Long vehicleId, Pageable pageable);

    ServiceRecord getRequiredServiceRecord(Long id);

    ServiceRecord createServiceRecord(ServiceForm form, String actorEmail);

    ServiceRecord updateServiceRecord(Long id, ServiceForm form, String actorEmail);

    void deleteServiceRecord(Long id, String actorEmail);

    List<ServiceRecord> getServiceHistoryForVehicle(Long vehicleId);
}
