package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateShipmentStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    private String locationName;
    private String description;

    public AdminUpdateShipmentStatusRequest() {}

    public AdminUpdateShipmentStatusRequest(OrderStatus status, String locationName, String description) {
        this.status = status;
        this.locationName = locationName;
        this.description = description;
    }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
