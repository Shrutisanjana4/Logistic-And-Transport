package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class CustomerRecentOrderDTO {
    private Long id;
    private String orderNumber;
    private String status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private String pickupCity;
    private String destinationCity;
    private String packageDescription;

    public CustomerRecentOrderDTO() {}

    public CustomerRecentOrderDTO(Long id, String orderNumber, String status, Double totalAmount,
                                  LocalDateTime createdAt, String pickupCity, String destinationCity,
                                  String packageDescription) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.pickupCity = pickupCity;
        this.destinationCity = destinationCity;
        this.packageDescription = packageDescription;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPickupCity() { return pickupCity; }
    public void setPickupCity(String pickupCity) { this.pickupCity = pickupCity; }
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public String getPackageDescription() { return packageDescription; }
    public void setPackageDescription(String packageDescription) { this.packageDescription = packageDescription; }
}
