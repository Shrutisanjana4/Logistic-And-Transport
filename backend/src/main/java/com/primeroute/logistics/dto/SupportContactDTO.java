package com.primeroute.logistics.dto;

public class SupportContactDTO {

    private String supportEmail;
    private String supportPhone;
    private String availability;
    private String supportHours;
    private String description;

    public SupportContactDTO() {
    }

    public SupportContactDTO(String supportEmail, String supportPhone, String availability, String description) {
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.availability = availability;
        this.supportHours = (availability != null && availability.contains("24/7")) ? "24/7" : availability;
        this.description = description;
    }

    public SupportContactDTO(String supportEmail, String supportPhone, String availability, String supportHours, String description) {
        this.supportEmail = supportEmail;
        this.supportPhone = supportPhone;
        this.availability = availability;
        this.supportHours = supportHours;
        this.description = description;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getSupportPhone() {
        return supportPhone;
    }

    public void setSupportPhone(String supportPhone) {
        this.supportPhone = supportPhone;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getSupportHours() {
        return supportHours != null ? supportHours : "24/7";
    }

    public void setSupportHours(String supportHours) {
        this.supportHours = supportHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}