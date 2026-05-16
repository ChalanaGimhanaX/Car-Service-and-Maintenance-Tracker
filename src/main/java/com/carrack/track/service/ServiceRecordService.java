package com.carrack.track.service;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.ServiceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRecordService {

    Page<ServiceRecord> searchServices(String keyword, String status, Pageable pageable);

    ServiceRecord getRequiredServiceRecord(Long id);

    ServiceRecord createServiceRecord(ServiceForm form, String actorEmail);

    ServiceRecord updateServiceRecord(Long id, ServiceForm form, String actorEmail);

    void deleteServiceRecord(Long id, String actorEmail);
}
