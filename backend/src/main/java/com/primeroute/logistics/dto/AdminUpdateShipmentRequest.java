package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.OrderStatus;

import java.time.LocalDate;

public class AdminUpdateShipmentRequest {

    private OrderStatus status;
    private String currentLocationName;
    private LocalDate estimatedDeliveryDate;

    public AdminUpdateShipmentRequest() {}

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getCurrentLocationName() { return currentLocationName; }
    public void setCurrentLocationName(String currentLocationName) { this.currentLocationName = currentLocationName; }

    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
}
