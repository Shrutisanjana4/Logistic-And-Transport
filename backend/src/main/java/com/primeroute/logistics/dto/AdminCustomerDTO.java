package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class AdminCustomerDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String companyName;
    private String status;
    private LocalDateTime createdAt;
    private long totalOrders;
    private long totalShipments;
    private Double totalSpent;

    public AdminCustomerDTO() {}

    public AdminCustomerDTO(Long id, String fullName, String email, String phone, String companyName,
                            String status, LocalDateTime createdAt, long totalOrders,
                            long totalShipments, Double totalSpent) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.status = status;
        this.createdAt = createdAt;
        this.totalOrders = totalOrders;
        this.totalShipments = totalShipments;
        this.totalSpent = totalSpent != null ? totalSpent : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
}
