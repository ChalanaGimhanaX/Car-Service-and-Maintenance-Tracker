package com.carrack.track.service.impl;

import com.carrack.track.dto.VehicleForm;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.VehicleStatus;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.VehicleService;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AuditService auditService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, AuditService auditService) {
        this.vehicleRepository = vehicleRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<Vehicle> searchVehicles(String keyword, String status, Pageable pageable) {
        Specification<Vehicle> spec = Specification.where((root, query, cb) -> cb.notEqual(root.get("status"), VehicleStatus.DELETED));

        if (StringUtils.hasText(keyword)) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("vehicleNumber")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern),
                    cb.like(cb.lower(root.get("model")), pattern),
                    cb.like(cb.lower(root.get("type")), pattern)
            ));
        }

        if (StringUtils.hasText(status)) {
            VehicleStatus parsed;
            try {
                parsed = VehicleStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid vehicle status.");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), parsed));
        }

        return vehicleRepository.findAll(spec, pageable);
    }

    @Override
    public Vehicle getRequiredVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));
        if (vehicle.getStatus() == VehicleStatus.DELETED) {
            throw new IllegalArgumentException("Vehicle not found.");
        }
        return vehicle;
    }

    @Override
    public Vehicle createVehicle(VehicleForm form, String actorEmail) {
        String vehicleNumber = normalizeVehicleNumber(form.getVehicleNumber());
        if (vehicleRepository.existsByVehicleNumberIgnoreCase(vehicleNumber)) {
            throw new IllegalStateException("Vehicle number already exists.");
        }

        Vehicle vehicle = new Vehicle();
        applyForm(vehicle, form);
        vehicle.setVehicleNumber(vehicleNumber);
        Vehicle saved = vehicleRepository.save(vehicle);
        auditService.log(AuditAction.VEHICLE_CREATED, actorEmail, saved.getVehicleNumber(), "Vehicle record created.");
        return saved;
    }

    @Override
    public Vehicle updateVehicle(Long id, VehicleForm form, String actorEmail) {
        Vehicle vehicle = getRequiredVehicle(id);
        String vehicleNumber = normalizeVehicleNumber(form.getVehicleNumber());
        Vehicle existing = vehicleRepository.findByVehicleNumberIgnoreCase(vehicleNumber).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalStateException("Vehicle number already exists.");
        }

        applyForm(vehicle, form);
        vehicle.setVehicleNumber(vehicleNumber);
        if (vehicle.getStatus() == VehicleStatus.DELETED) {
            vehicle.setDeletedAt(LocalDateTime.now());
        } else {
            vehicle.setDeletedAt(null);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        auditService.log(AuditAction.VEHICLE_UPDATED, actorEmail, saved.getVehicleNumber(), "Vehicle record updated.");
        return saved;
    }

    @Override
    public void deleteVehicle(Long id, String actorEmail) {
        Vehicle vehicle = getRequiredVehicle(id);
        vehicle.setStatus(VehicleStatus.DELETED);
        vehicle.setDeletedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);
        auditService.log(AuditAction.VEHICLE_DELETED, actorEmail, vehicle.getVehicleNumber(), "Vehicle archived with soft delete.");
    }

    private void applyForm(Vehicle vehicle, VehicleForm form) {
        vehicle.setModel(form.getModel().trim());
        vehicle.setType(form.getType().trim());
        vehicle.setBrand(form.getBrand().trim());
        vehicle.setFuelType(form.getFuelType());
        vehicle.setYear(form.getYear());
        vehicle.setMileage(form.getMileage());
        vehicle.setStatus(form.getStatus());
        vehicle.setNotes(StringUtils.hasText(form.getNotes()) ? form.getNotes().trim() : null);
    }

    private String normalizeVehicleNumber(String vehicleNumber) {
        return vehicleNumber.trim().replaceAll("\\s+", " ").toUpperCase();
    }
}
