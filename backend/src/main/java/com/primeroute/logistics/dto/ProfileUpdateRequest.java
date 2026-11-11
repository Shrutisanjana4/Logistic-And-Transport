package com.primeroute.logistics.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^(?!\\d+$)[a-zA-Z0-9\\s.'-]+$",
        message = "Full name must contain letters and cannot be purely numeric"
    )
    private String fullName;

    @Pattern(
        regexp = "^$|^(?:\\+91[\\-\\s]?)?[6-9]\\d{9}$",
        message = "Please provide a valid 10-digit Indian mobile number (e.g., +91 9876543210 or 9876543210)"
    )
    private String phone;

    private String companyName;

    private String businessType;

    @Email(message = "Invalid business email format")
    private String businessEmail;

    @Pattern(
        regexp = "^$|^(?:\\+91[\\-\\s]?)?[6-9]\\d{9}$",
        message = "Please provide a valid 10-digit Indian business phone number (e.g., +91 9876543210 or 9876543210)"
    )
    private String businessPhone;

    private String registrationNumber;

    private String gstin;

    private String businessAddress;

    public ProfileUpdateRequest() {
    }

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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessEmail() {
        return businessEmail;
    }

    public void setBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }
}