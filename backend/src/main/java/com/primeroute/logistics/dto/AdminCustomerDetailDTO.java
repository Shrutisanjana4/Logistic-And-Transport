package com.primeroute.logistics.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminCustomerDetailDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String companyName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastPasswordChange;
    private long totalOrders;
    private long totalShipments;
    private long activeShipments;
    private long deliveredShipments;
    private Double totalSpent;
    private List<AddressResponse> addresses = new ArrayList<>();
    private List<CustomerRecentOrderDTO> recentOrders = new ArrayList<>();

    public AdminCustomerDetailDTO() {}

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
    public LocalDateTime getLastPasswordChange() { return lastPasswordChange; }
    public void setLastPasswordChange(LocalDateTime lastPasswordChange) { this.lastPasswordChange = lastPasswordChange; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
    public long getActiveShipments() { return activeShipments; }
    public void setActiveShipments(long activeShipments) { this.activeShipments = activeShipments; }
    public long getDeliveredShipments() { return deliveredShipments; }
    public void setDeliveredShipments(long deliveredShipments) { this.deliveredShipments = deliveredShipments; }
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
    public List<AddressResponse> getAddresses() { return addresses; }
    public void setAddresses(List<AddressResponse> addresses) { this.addresses = addresses; }
    public List<CustomerRecentOrderDTO> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<CustomerRecentOrderDTO> recentOrders) { this.recentOrders = recentOrders; }
}
