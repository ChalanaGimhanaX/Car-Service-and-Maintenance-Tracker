package com.carrack.track.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Simple form backing object used when a user updates their profile.
 * Kept minimal intentionally: validation annotations enforce basic rules
 * so controllers and services can assume reasonably clean input.
 */
public class ProfileForm {

    /** User's display name shown across the UI. Required and limited in length. */
    @NotBlank
    @Size(max = 120)
    private String fullName;

    /** Optional phone number used for contact; validated for max length only. */
    @Size(max = 30)
    private String phone;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
