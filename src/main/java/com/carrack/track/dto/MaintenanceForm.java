package com.carrack.track.dto;

import com.carrack.track.enums.ReminderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public class MaintenanceForm {

    @NotBlank(message = "Vehicle number is required.")
    @Size(max = 30, message = "Vehicle number must be 30 characters or fewer.")
    @Pattern(regexp = "^[A-Za-z0-9 -]+$", message = "Vehicle number may only contain letters, numbers, spaces, and hyphens.")
    private String vehicleNumber;

    @NotBlank(message = "Reminder title is required.")
    @Size(max = 120, message = "Reminder title must be 120 characters or fewer.")
    private String title;

    @NotNull(message = "Reminder date is required.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reminderDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastServiceDate;

    @Min(value = 0, message = "Mileage interval cannot be negative.")
    private Integer mileageInterval;

    @NotNull(message = "Status is required.")
    private ReminderStatus status = ReminderStatus.UPCOMING;

    @Size(max = 1000, message = "Notes must be 1000 characters or fewer.")
    private String notes;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Integer getMileageInterval() {
        return mileageInterval;
    }

    public void setMileageInterval(Integer mileageInterval) {
        this.mileageInterval = mileageInterval;
    }

    public ReminderStatus getStatus() {
        return status;
    }

    public void setStatus(ReminderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
