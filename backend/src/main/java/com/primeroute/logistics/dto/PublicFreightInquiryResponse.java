package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class PublicFreightInquiryResponse {

    private String inquiryNumber;
    private String inquiryType;
    private String companyName;
    private String contactName;
    private String email;
    private String phone;
    private String origin;
    private String destination;
    private String serviceType;
    private Double weight;
    private String status;
    private LocalDateTime createdAt;
    private String message;

    public PublicFreightInquiryResponse() {
    }

    public PublicFreightInquiryResponse(
            String inquiryNumber,
            String inquiryType,
            String companyName,
            String contactName,
            String email,
            String phone,
            String origin,
            String destination,
            String serviceType,
            Double weight,
            String status,
            LocalDateTime createdAt,
            String message) {
        this.inquiryNumber = inquiryNumber;
        this.inquiryType = inquiryType;
        this.companyName = companyName;
        this.contactName = contactName;
        this.email = email;
        this.phone = phone;
        this.origin = origin;
        this.destination = destination;
        this.serviceType = serviceType;
        this.weight = weight;
        this.status = status;
        this.createdAt = createdAt;
        this.message = message;
    }

    public String getInquiryNumber() {
        return inquiryNumber;
    }

    public void setInquiryNumber(String inquiryNumber) {
        this.inquiryNumber = inquiryNumber;
    }

    public String getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(String inquiryType) {
        this.inquiryType = inquiryType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
