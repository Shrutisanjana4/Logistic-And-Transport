package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.OrderStatus;
import java.time.LocalDateTime;

public class RouteShipmentDTO {

    private Long id;
    private String trackingNumber;
    private String orderNumber;
    private OrderStatus status;
    private String currentLocationName;
    private Double currentLatitude;
    private Double currentLongitude;
    private String latestEventTitle;
    private String latestEventLocation;
    private LocalDateTime latestEventTime;

    public RouteShipmentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getCurrentLocationName() { return currentLocationName; }
    public void setCurrentLocationName(String currentLocationName) { this.currentLocationName = currentLocationName; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public String getLatestEventTitle() { return latestEventTitle; }
    public void setLatestEventTitle(String latestEventTitle) { this.latestEventTitle = latestEventTitle; }

    public String getLatestEventLocation() { return latestEventLocation; }
    public void setLatestEventLocation(String latestEventLocation) { this.latestEventLocation = latestEventLocation; }

    public LocalDateTime getLatestEventTime() { return latestEventTime; }
    public void setLatestEventTime(LocalDateTime latestEventTime) { this.latestEventTime = latestEventTime; }
}