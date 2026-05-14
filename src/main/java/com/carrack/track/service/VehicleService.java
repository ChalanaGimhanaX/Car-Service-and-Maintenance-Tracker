package com.carrack.track.service;

import com.carrack.track.dto.VehicleForm;
import com.carrack.track.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {

    Page<Vehicle> searchVehicles(String keyword, String status, Pageable pageable);

    Vehicle getRequiredVehicle(Long id);

    Vehicle createVehicle(VehicleForm form, String actorEmail);

    Vehicle updateVehicle(Long id, VehicleForm form, String actorEmail);

    void deleteVehicle(Long id, String actorEmail);
}
