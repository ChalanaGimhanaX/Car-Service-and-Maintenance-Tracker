package com.carrack.track.dto;

import com.carrack.track.enums.FuelType;
import com.carrack.track.enums.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VehicleForm {

    private Long ownerId;

    @NotBlank(message = "Vehicle number is required.")
    @Size(max = 30, message = "Vehicle number must be 30 characters or fewer.")
    @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "Vehicle number may only contain letters, numbers, spaces, and hyphens.")
    private String vehicleNumber;

    @NotBlank(message = "Model is required.")
    @Size(max = 100, message = "Model must be 100 characters or fewer.")
    private String model;

    @NotBlank(message = "Type is required.")
    @Size(max = 60, message = "Type must be 60 characters or fewer.")
    private String type;

    @NotBlank(message = "Brand is required.")
    @Size(max = 80, message = "Brand must be 80 characters or fewer.")
    private String brand;

    @NotNull(message = "Fuel type is required.")
    private FuelType fuelType;

    @NotNull(message = "Year is required.")
    @Min(value = 1900, message = "Year must be 1900 or later.")
    @Max(value = 2100, message = "Year must be 2100 or earlier.")
    private Integer year;

    @NotNull(message = "Mileage is required.")
    @Min(value = 0, message = "Mileage cannot be negative.")
    private Integer mileage;

    @NotNull(message = "Status is required.")
    private VehicleStatus status = VehicleStatus.ACTIVE;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer.")
    private String notes;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
