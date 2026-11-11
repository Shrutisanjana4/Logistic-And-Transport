package com.primeroute.logistics.dto.driver;

import java.util.ArrayList;
import java.util.List;

public class DriverRouteDTO {
    private Long shipmentId;
    private String trackingNumber;
    private String orderNumber;
    private String status;
    private String operationalStage;
    private String routeCode;
    private DriverLocationDTO origin;
    private DriverLocationDTO currentLocation;
    private DriverLocationDTO destination;
    private Double distanceRemainingKm;
    private Double totalDistanceKm;
    private Integer estimatedDurationMinutes;
    private String estimatedDeliveryTime;
    private String lastLocationUpdatedAt;
    private String freshness; // LIVE, RECENT, LAST_RECORDED, OFFLINE
    private List<List<Double>> routeGeometry = new ArrayList<>();
    private List<List<Double>> fullRouteGeometry = new ArrayList<>();
    private List<DriverRouteStepDTO> routeSteps = new ArrayList<>();
    private List<DriverRouteWaypointDTO> waypoints = new ArrayList<>();

    public DriverRouteDTO() {}

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperationalStage() { return operationalStage; }
    public void setOperationalStage(String operationalStage) { this.operationalStage = operationalStage; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public DriverLocationDTO getOrigin() { return origin; }
    public void setOrigin(DriverLocationDTO origin) { this.origin = origin; }

    public DriverLocationDTO getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(DriverLocationDTO currentLocation) { this.currentLocation = currentLocation; }

    public DriverLocationDTO getDestination() { return destination; }
    public void setDestination(DriverLocationDTO destination) { this.destination = destination; }

    public Double getDistanceRemainingKm() { return distanceRemainingKm; }
    public void setDistanceRemainingKm(Double distanceRemainingKm) { this.distanceRemainingKm = distanceRemainingKm; }

    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(Integer estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public String getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public String getLastLocationUpdatedAt() { return lastLocationUpdatedAt; }
    public void setLastLocationUpdatedAt(String lastLocationUpdatedAt) { this.lastLocationUpdatedAt = lastLocationUpdatedAt; }

    public String getFreshness() { return freshness; }
    public void setFreshness(String freshness) { this.freshness = freshness; }

    public List<List<Double>> getRouteGeometry() { return routeGeometry; }
    public void setRouteGeometry(List<List<Double>> routeGeometry) { this.routeGeometry = routeGeometry; }

    public List<List<Double>> getFullRouteGeometry() { return fullRouteGeometry; }
    public void setFullRouteGeometry(List<List<Double>> fullRouteGeometry) { this.fullRouteGeometry = fullRouteGeometry; }

    public List<DriverRouteStepDTO> getRouteSteps() { return routeSteps; }
    public void setRouteSteps(List<DriverRouteStepDTO> routeSteps) { this.routeSteps = routeSteps; }

    public List<DriverRouteWaypointDTO> getWaypoints() { return waypoints; }
    public void setWaypoints(List<DriverRouteWaypointDTO> waypoints) { this.waypoints = waypoints; }
}
