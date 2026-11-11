package com.primeroute.logistics.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PublicFreightInquiryRequest {

    @NotBlank(message = "Enter a valid company name.")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9\\s&.,\\-'/()]{2,100}$",
            message = "Enter a valid company name."
    )
    private String companyName;

    @NotBlank(message = "Enter a valid contact name.")
    @Size(min = 2, max = 100, message = "Contact name must be between 2 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])[a-zA-Z\\s.'-]{2,100}$",
            message = "Enter a valid contact name."
    )
    @JsonAlias({"fullName", "contactPerson"})
    private String contactName;

    @NotBlank(message = "Enter a valid business email address.")
    @Size(max = 120, message = "Email address must not exceed 120 characters.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "Enter a valid business email address."
    )
    @JsonAlias({"workEmail"})
    private String email;

    @NotBlank(message = "Enter a valid phone number.")
    @Pattern(
            regexp = "^(?:\\+91[\\s-]?)?[6-9](?:[\\s-]?\\d){9}$",
            message = "Enter a valid phone number."
    )
    private String phone;

    @NotBlank(message = "Enter a valid origin city or terminal.")
    @Size(min = 2, max = 100, message = "Origin must be between 2 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9\\s.,\\-'/()]{2,100}$",
            message = "Enter a valid origin city or terminal."
    )
    private String origin;

    @NotBlank(message = "Enter a valid destination city or hub.")
    @Size(min = 2, max = 100, message = "Destination must be between 2 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9\\s.,\\-'/()]{2,100}$",
            message = "Enter a valid destination city or hub."
    )
    private String destination;

    @NotBlank(message = "Please select a valid service type.")
    @Size(max = 64, message = "Service type must not exceed 64 characters.")
    @JsonAlias({"serviceRequired"})
    private String serviceType;

    @DecimalMin(value = "0.01", message = "Enter a valid cargo weight greater than 0.")
    @DecimalMax(value = "100000.00", message = "Cargo weight exceeds maximum allowed capacity (100,000 kg).")
    private Double weight;

    @Size(max = 64, message = "Cargo type must not exceed 64 characters.")
    private String cargoType;

    @Size(max = 1000, message = "Additional specifications must not exceed 1000 characters.")
    private String message;

    private String inquiryType; // QUOTATION or CONSULTATION

    public PublicFreightInquiryRequest() {
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName != null ? companyName.trim() : null;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName != null ? contactName.trim() : null;
    }

    public void setFullName(String fullName) {
        setContactName(fullName);
    }

    public void setContactPerson(String contactPerson) {
        setContactName(contactPerson);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : null;
    }

    public void setWorkEmail(String workEmail) {
        setEmail(workEmail);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin != null ? origin.trim() : null;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination != null ? destination.trim() : null;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType != null ? serviceType.trim() : null;
    }

    public void setServiceRequired(String serviceRequired) {
        setServiceType(serviceRequired);
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType != null ? cargoType.trim() : null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message.trim() : null;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(String inquiryType) {
        this.inquiryType = inquiryType != null ? inquiryType.trim() : null;
    }
}
