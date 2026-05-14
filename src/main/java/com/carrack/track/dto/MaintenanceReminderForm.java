package com.carrack.track.dto;

import com.carrack.track.enums.MaintenancePriority;
import com.carrack.track.enums.MaintenanceStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class MaintenanceReminderForm {

    @NotBlank(message = "Reminder title is required.")
    @Size(max = 120, message = "Reminder title must be 120 characters or fewer.")
    private String title;

    @NotNull(message = "Vehicle is required.")
    private Long vehicleId;

    private LocalDate lastServiceDate;

    @Min(value = 1, message = "Service interval must be at least 1 day.")
    private Integer intervalDays;

    private LocalDate dueDate;

    @NotNull(message = "Status is required.")
    private MaintenanceStatus status = MaintenanceStatus.UPCOMING;

    @NotNull(message = "Priority is required.")
    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    @Size(max = 1000, message = "Note must be 1000 characters or fewer.")
    private String note;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    public MaintenancePriority getPriority() {
        return priority;
    }

    public void setPriority(MaintenancePriority priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
