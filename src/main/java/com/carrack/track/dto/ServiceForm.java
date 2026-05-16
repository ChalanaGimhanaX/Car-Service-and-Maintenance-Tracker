package com.carrack.track.dto;

import com.carrack.track.enums.ServiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class ServiceForm {

    @NotBlank(message = "Service code is required.")
    @Size(max = 30, message = "Service code must be 30 characters or fewer.")
    @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "Service code may only contain letters, numbers, spaces, and hyphens.")
    private String serviceCode;

    @NotBlank(message = "Vehicle number is required.")
    @Size(max = 30, message = "Vehicle number must be 30 characters or fewer.")
    @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "Vehicle number may only contain letters, numbers, spaces, and hyphens.")
    private String vehicleNumber;

    @NotBlank(message = "Customer name is required.")
    @Size(max = 120, message = "Customer name must be 120 characters or fewer.")
    private String customerName;

    @NotBlank(message = "Service type is required.")
    @Size(max = 80, message = "Service type must be 80 characters or fewer.")
    private String serviceType;

    @NotNull(message = "Service date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate serviceDate;

    @NotNull(message = "Cost is required.")
    @DecimalMin(value = "0.00", message = "Cost cannot be negative.")
    private BigDecimal cost;

    @NotBlank(message = "Technician name is required.")
    @Size(max = 120, message = "Technician name must be 120 characters or fewer.")
    private String technicianName;

    @NotNull(message = "Status is required.")
    private ServiceStatus status = ServiceStatus.PENDING;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer.")
    private String notes;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
