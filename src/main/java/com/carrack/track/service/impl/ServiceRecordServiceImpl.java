package com.carrack.track.service.impl;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.ServiceRecordService;
import com.carrack.track.service.VehicleService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleService vehicleService;
    private final AuditService auditService;

    public ServiceRecordServiceImpl(ServiceRecordRepository serviceRecordRepository,
                                    VehicleService vehicleService,
                                    AuditService auditService) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.vehicleService = vehicleService;
        this.auditService = auditService;
    }

    @Override
    public Page<ServiceRecord> searchServiceRecords(String keyword, Long vehicleId, Pageable pageable) {
        Specification<ServiceRecord> spec = Specification.where(null);

        if (vehicleId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("vehicle").get("id"), vehicleId));
        }

        if (StringUtils.hasText(keyword)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("serviceType")), pattern),
                    cb.like(cb.lower(root.get("serviceCenter")), pattern),
                    cb.like(cb.lower(root.get("vehicle").get("vehicleNumber")), pattern)
            ));
        }

        return serviceRecordRepository.findAll(spec, pageable);
    }

    @Override
    public ServiceRecord getRequiredServiceRecord(Long id) {
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service record not found."));
    }

    @Override
    public ServiceRecord createServiceRecord(ServiceForm form, String actorEmail) {
        Vehicle vehicle = vehicleService.getRequiredVehicle(form.getVehicleId());

        ServiceRecord record = new ServiceRecord();
        applyForm(record, form, vehicle);
        ServiceRecord saved = serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_CREATED, actorEmail,
                vehicle.getVehicleNumber(), "Service record created.");
        return saved;
    }

    @Override
    public ServiceRecord updateServiceRecord(Long id, ServiceForm form, String actorEmail) {
        ServiceRecord record = getRequiredServiceRecord(id);
        Vehicle vehicle = vehicleService.getRequiredVehicle(form.getVehicleId());

        applyForm(record, form, vehicle);
        ServiceRecord saved = serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_UPDATED, actorEmail,
                vehicle.getVehicleNumber(), "Service record updated.");
        return saved;
    }

    @Override
    public void deleteServiceRecord(Long id, String actorEmail) {
        ServiceRecord record = getRequiredServiceRecord(id);
        String vehicleNumber = record.getVehicle().getVehicleNumber();
        serviceRecordRepository.delete(record);
        auditService.log(AuditAction.SERVICE_DELETED, actorEmail,
                vehicleNumber, "Service record deleted.");
    }

    @Override
    public List<ServiceRecord> getServiceHistoryForVehicle(Long vehicleId) {
        return serviceRecordRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId);
    }

    private void applyForm(ServiceRecord record, ServiceForm form, Vehicle vehicle) {
        record.setVehicle(vehicle);
        record.setServiceType(form.getServiceType().trim());
        record.setServiceDate(form.getServiceDate());
        record.setMileageAtService(form.getMileageAtService());
        record.setServiceCenter(StringUtils.hasText(form.getServiceCenter()) ? form.getServiceCenter().trim() : null);
        record.setCost(form.getCost());
        record.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
    }
}
