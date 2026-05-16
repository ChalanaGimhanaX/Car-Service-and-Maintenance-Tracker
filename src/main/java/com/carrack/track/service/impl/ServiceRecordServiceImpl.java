package com.carrack.track.service.impl;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.ServiceStatus;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.ServiceRecordService;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final AuditService auditService;

    public ServiceRecordServiceImpl(ServiceRecordRepository serviceRecordRepository, AuditService auditService) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<ServiceRecord> searchServices(String keyword, String status, Pageable pageable) {
        Specification<ServiceRecord> spec = Specification.where((root, query, cb) -> cb.isNull(root.get("deletedAt")));

        if (StringUtils.hasText(keyword)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("serviceCode")), pattern),
                    cb.like(cb.lower(root.get("vehicleNumber")), pattern),
                    cb.like(cb.lower(root.get("customerName")), pattern),
                    cb.like(cb.lower(root.get("serviceType")), pattern),
                    cb.like(cb.lower(root.get("technicianName")), pattern)
            ));
        }

        if (StringUtils.hasText(status)) {
            ServiceStatus parsed;
            try {
                parsed = ServiceStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid service status.");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), parsed));
        }

        return serviceRecordRepository.findAll(spec, pageable);
    }

    @Override
    public ServiceRecord getRequiredServiceRecord(Long id) {
        ServiceRecord record = serviceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service record not found."));
        if (record.getDeletedAt() != null) {
            throw new IllegalArgumentException("Service record not found.");
        }
        return record;
    }

    @Override
    public ServiceRecord createServiceRecord(ServiceForm form, String actorEmail) {
        String serviceCode = normalizeCode(form.getServiceCode());
        if (serviceRecordRepository.existsByServiceCodeIgnoreCase(serviceCode)) {
            throw new IllegalStateException("Service code already exists.");
        }

        ServiceRecord record = new ServiceRecord();
        applyForm(record, form);
        record.setServiceCode(serviceCode);
        record.setVehicleNumber(normalizeCode(form.getVehicleNumber()));
        ServiceRecord saved = serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_CREATED, actorEmail, saved.getServiceCode(), "Service record created.");
        return saved;
    }

    @Override
    public ServiceRecord updateServiceRecord(Long id, ServiceForm form, String actorEmail) {
        ServiceRecord record = getRequiredServiceRecord(id);
        String serviceCode = normalizeCode(form.getServiceCode());
        ServiceRecord existing = serviceRecordRepository.findByServiceCodeIgnoreCase(serviceCode).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalStateException("Service code already exists.");
        }

        applyForm(record, form);
        record.setServiceCode(serviceCode);
        record.setVehicleNumber(normalizeCode(form.getVehicleNumber()));
        ServiceRecord saved = serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_UPDATED, actorEmail, saved.getServiceCode(), "Service record updated.");
        return saved;
    }

    @Override
    public void deleteServiceRecord(Long id, String actorEmail) {
        ServiceRecord record = getRequiredServiceRecord(id);
        record.setDeletedAt(LocalDateTime.now());
        serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_DELETED, actorEmail, record.getServiceCode(), "Service record archived.");
    }

    private void applyForm(ServiceRecord record, ServiceForm form) {
        record.setCustomerName(form.getCustomerName().trim());
        record.setServiceType(form.getServiceType().trim());
        record.setServiceDate(form.getServiceDate());
        record.setCost(form.getCost());
        record.setTechnicianName(form.getTechnicianName().trim());
        record.setStatus(form.getStatus());
        record.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
    }

    private String normalizeCode(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}
