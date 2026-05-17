package com.carrack.track.service.impl;

import com.carrack.track.dto.ServiceForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.ServiceRecord;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.Role;
import com.carrack.track.enums.ServiceStatus;
import com.carrack.track.repository.InvoiceRepository;
import com.carrack.track.repository.ServiceRecordRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.ServiceRecordService;
import com.carrack.track.service.VehicleService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleService vehicleService;
    private final InvoiceRepository invoiceRepository;
    private final AuditService auditService;

    public ServiceRecordServiceImpl(ServiceRecordRepository serviceRecordRepository,
                                    VehicleService vehicleService,
                                    InvoiceRepository invoiceRepository,
                                    AuditService auditService) {
        this.serviceRecordRepository = serviceRecordRepository;
        this.vehicleService = vehicleService;
        this.invoiceRepository = invoiceRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<ServiceRecord> searchServiceRecords(String keyword, Long vehicleId, AppUser currentUser, Pageable pageable) {
        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        if (!isAdmin(currentUser)) {
            return serviceRecordRepository.searchRecordsByOwner(currentUser.getId(), cleanedKeyword, vehicleId, pageable);
        }
        return serviceRecordRepository.searchRecords(cleanedKeyword, vehicleId, pageable);
    }

    @Override
    public ServiceRecord getRequiredServiceRecord(Long id) {
        return serviceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service record not found."));
    }

    @Override
    public ServiceRecord getRequiredServiceRecord(Long id, AppUser currentUser) {
        ServiceRecord record = getRequiredServiceRecord(id);
        if (!isAdmin(currentUser) && !record.getVehicle().getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Service record not found.");
        }
        return record;
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
    public ServiceRecord updateServiceStatus(Long id, ServiceStatus status, String actorEmail) {
        if (status == null) {
            throw new IllegalArgumentException("Service progress is required.");
        }
        ServiceRecord record = getRequiredServiceRecord(id);
        record.setServiceStatus(status);
        ServiceRecord saved = serviceRecordRepository.save(record);
        auditService.log(AuditAction.SERVICE_UPDATED, actorEmail,
                saved.getVehicle().getVehicleNumber(), "Service progress updated.");
        return saved;
    }

    @Override
    public void deleteServiceRecord(Long id, String actorEmail) {
        ServiceRecord record = getRequiredServiceRecord(id);
        if (invoiceRepository.existsByServiceRecordId(id)) {
            throw new IllegalStateException("Delete the invoice first before deleting this service record.");
        }
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
        record.setServiceStatus(form.getServiceStatus() != null ? form.getServiceStatus() : ServiceStatus.PENDING);
        record.setCost(form.getCost());
        record.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
    }

    private boolean isAdmin(AppUser user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
