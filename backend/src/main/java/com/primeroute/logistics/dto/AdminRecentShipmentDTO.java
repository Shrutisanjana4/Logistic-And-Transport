package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class AdminRecentShipmentDTO {
    private Long id;
    private String trackingNumber;
    private String orderNumber;
    private String origin;
    private String destination;
    private String route;
    private String status;
    private String currentLocation;
    private LocalDateTime createdAt;

    public AdminRecentShipmentDTO() {
    }

    public AdminRecentShipmentDTO(Long id, String trackingNumber, String orderNumber, String origin, String destination, String route, String status, String currentLocation, LocalDateTime createdAt) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.orderNumber = orderNumber;
        this.origin = origin;
        this.destination = destination;
        this.route = route;
        this.status = status;
        this.currentLocation = currentLocation;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
