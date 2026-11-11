package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class SupportTicketDTO {

    private Long id;
    private String ticketNumber;
    private String subject;
    private String category;
    private String priority;
    private String status;
    private String description;
    private String orderNumber;
    private String trackingNumber;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportTicketDTO() {
    }

    public SupportTicketDTO(Long id, String ticketNumber, String subject, String category,
                            String priority, String status, String description,
                            String orderNumber, String trackingNumber,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(id, ticketNumber, subject, category, priority, status, description,
             orderNumber, trackingNumber, null, null, null, createdAt, updatedAt);
    }

    public SupportTicketDTO(Long id, String ticketNumber, String subject, String category,
                            String priority, String status, String description,
                            String orderNumber, String trackingNumber,
                            String customerName, String customerEmail, String customerPhone,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.subject = subject;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.description = description;
        this.orderNumber = orderNumber;
        this.trackingNumber = trackingNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}