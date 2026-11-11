package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminSupportSettingsDTO {
    @NotBlank(message = "Support email is required")
    private String supportEmail;

    @NotBlank(message = "Support phone is required")
    private String supportPhone;

    @NotBlank(message = "Availability hours are required")
    private String availability;

    private String description;

    public AdminSupportSettingsDTO() {}

    public AdminSupportSettingsDTO(String supportEmail, String supportPhone, String availability, String description) {
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.availability = availability;
        this.description = description;
    }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getSupportPhone() { return supportPhone; }
    public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
