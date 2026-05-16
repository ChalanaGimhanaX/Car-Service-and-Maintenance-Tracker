package com.carrack.track.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ServiceForm {

    @NotNull(message = "Vehicle is required.")
    private Long vehicleId;

    @NotBlank(message = "Service type is required.")
    @Size(max = 100, message = "Service type must be 100 characters or fewer.")
    private String serviceType;

    @NotNull(message = "Service date is required.")
    @PastOrPresent(message = "Service date cannot be in the future.")
    private LocalDate serviceDate;

    @NotNull(message = "Mileage at service is required.")
    @Min(value = 0, message = "Mileage cannot be negative.")
    private Integer mileageAtService;

    @Size(max = 150, message = "Service center must be 150 characters or fewer.")
    private String serviceCenter;

    @NotNull(message = "Cost is required.")
    @DecimalMin(value = "0.00", message = "Cost cannot be negative.")
    @Digits(integer = 8, fraction = 2, message = "Cost must be a valid amount.")
    private BigDecimal cost;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer.")
    private String notes;

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public Integer getMileageAtService() { return mileageAtService; }
    public void setMileageAtService(Integer mileageAtService) { this.mileageAtService = mileageAtService; }

    public String getServiceCenter() { return serviceCenter; }
    public void setServiceCenter(String serviceCenter) { this.serviceCenter = serviceCenter; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
