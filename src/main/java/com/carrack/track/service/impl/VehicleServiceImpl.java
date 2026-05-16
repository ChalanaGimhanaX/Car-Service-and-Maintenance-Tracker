package com.carrack.track.service.impl;

import com.carrack.track.dto.VehicleForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Vehicle;
import com.carrack.track.enums.AuditAction;
import com.carrack.track.enums.AccountStatus;
import com.carrack.track.enums.Role;
import com.carrack.track.enums.VehicleStatus;
import com.carrack.track.repository.AppUserRepository;
import com.carrack.track.repository.VehicleRepository;
import com.carrack.track.service.AuditService;
import com.carrack.track.service.VehicleService;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AppUserRepository userRepository;
    private final AuditService auditService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, AppUserRepository userRepository, AuditService auditService) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Override
    public Page<Vehicle> searchVehicles(String keyword, String status, AppUser currentUser, Pageable pageable) {
        String cleanedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        VehicleStatus parsedStatus = null;
        if (StringUtils.hasText(status)) {
            try {
                parsedStatus = VehicleStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid vehicle status.");
            }
        }

        if (isAdmin(currentUser)) {
            return vehicleRepository.searchAll(cleanedKeyword, parsedStatus, pageable);
        }
        return vehicleRepository.searchByOwner(currentUser.getId(), cleanedKeyword, parsedStatus, pageable);
    }

    @Override
    public Vehicle getRequiredVehicle(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found."));
    }

    @Override
    public Vehicle getRequiredVehicle(Long id, AppUser currentUser) {
        Vehicle vehicle = getRequiredVehicle(id);
        if (vehicle.getStatus() == VehicleStatus.DELETED || !canAccess(vehicle, currentUser)) {
            throw new IllegalArgumentException("Vehicle not found.");
        }
        return vehicle;
    }

    @Override
    public Vehicle createVehicle(VehicleForm form, AppUser currentUser, String actorEmail) {
        String vehicleNumber = normalizeVehicleNumber(form.getVehicleNumber());
        if (vehicleRepository.existsByVehicleNumberIgnoreCase(vehicleNumber)) {
            throw new IllegalStateException("Vehicle number already exists.");
        }

        Vehicle vehicle = new Vehicle();
        applyForm(vehicle, form);
        vehicle.setOwner(resolveOwner(form, currentUser));
        vehicle.setVehicleNumber(vehicleNumber);
        Vehicle saved = vehicleRepository.save(vehicle);
        auditService.log(AuditAction.VEHICLE_CREATED, actorEmail, saved.getVehicleNumber(), "Vehicle record created.");
        return saved;
    }

    @Override
    public Vehicle updateVehicle(Long id, VehicleForm form, AppUser currentUser, String actorEmail) {
        Vehicle vehicle = getRequiredVehicle(id, currentUser);
        String vehicleNumber = normalizeVehicleNumber(form.getVehicleNumber());
        Vehicle existing = vehicleRepository.findByVehicleNumberIgnoreCase(vehicleNumber).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new IllegalStateException("Vehicle number already exists.");
        }

        applyForm(vehicle, form);
        vehicle.setOwner(resolveOwner(form, currentUser));
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
    public void deleteVehicle(Long id, AppUser currentUser, String actorEmail) {
        Vehicle vehicle = getRequiredVehicle(id, currentUser);
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

    private AppUser resolveOwner(VehicleForm form, AppUser currentUser) {
        if (!isAdmin(currentUser)) {
            return currentUser;
        }
        if (form.getOwnerId() == null) {
            throw new IllegalStateException("Vehicle owner is required.");
        }
        AppUser owner = userRepository.findById(form.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Selected owner was not found."));
        if (owner.getRole() != Role.CLIENT || owner.getStatus() == AccountStatus.DELETED) {
            throw new IllegalStateException("Vehicle owner must be a client account.");
        }
        return owner;
    }

    private boolean canAccess(Vehicle vehicle, AppUser currentUser) {
        return isAdmin(currentUser) || vehicle.getOwner().getId().equals(currentUser.getId());
    }

    private boolean isAdmin(AppUser user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
