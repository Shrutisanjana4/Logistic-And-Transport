package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminProfileUpdateRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String phone;

    public AdminProfileUpdateRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
