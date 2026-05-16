package com.carrack.track.service;

import com.carrack.track.dto.VehicleForm;
import com.carrack.track.entity.AppUser;
import com.carrack.track.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {

    Page<Vehicle> searchVehicles(String keyword, String status, AppUser currentUser, Pageable pageable);

    Vehicle getRequiredVehicle(Long id);

    Vehicle getRequiredVehicle(Long id, AppUser currentUser);

    Vehicle createVehicle(VehicleForm form, AppUser currentUser, String actorEmail);

    Vehicle updateVehicle(Long id, VehicleForm form, AppUser currentUser, String actorEmail);

    void deleteVehicle(Long id, AppUser currentUser, String actorEmail);
}
