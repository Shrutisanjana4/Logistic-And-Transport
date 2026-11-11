package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.DriverStatus;
import com.primeroute.logistics.entity.RouteStatus;
import com.primeroute.logistics.entity.VehicleType;

import java.time.LocalDateTime;

public class AdminRouteDTO {

    private Long id;
    private String routeCode;
    private String routeName;
    private String origin;
    private String originName;
    private String originCity;
    private String originState;
    private String destinationName;
    private String destinationCity;
    private String destinationState;
    private Double distance;
    private Double estimatedTransit;
    private Double estimatedTransitHours;
    private String destination;
    private String originAddress;
    private String destinationAddress;
    private Double distanceKm;
    private Double estimatedTransitTimeHours;
    private RouteStatus status;

    // Driver details
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private DriverStatus driverStatus;

    // Vehicle details
    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleModel;
    private VehicleType vehicleType;
    private Double vehicleCapacity;
    private String vehicleCapacityUnit;

    // Active loads & telemetry
    private long activeLoads;
    private long totalShipments;

    // Geographic coordinates
    private Double originLatitude;
    private Double originLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminRouteDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getRouteName() { return routeName != null ? routeName : (origin != null && destination != null ? origin + " → " + destination : routeCode); }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getOriginName() { return originName != null ? originName : origin; }
    public void setOriginName(String originName) { this.originName = originName; }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }

    public String getOriginState() { return originState; }
    public void setOriginState(String originState) { this.originState = originState; }

    public String getDestinationName() { return destinationName != null ? destinationName : destination; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDestinationState() { return destinationState; }
    public void setDestinationState(String destinationState) { this.destinationState = destinationState; }

    public Double getDistance() { return distance != null ? distance : distanceKm; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Double getEstimatedTransit() { return estimatedTransit != null ? estimatedTransit : (estimatedTransitHours != null ? estimatedTransitHours : estimatedTransitTimeHours); }
    public void setEstimatedTransit(Double estimatedTransit) { this.estimatedTransit = estimatedTransit; }

    public Double getEstimatedTransitHours() { return estimatedTransitHours != null ? estimatedTransitHours : estimatedTransitTimeHours; }
    public void setEstimatedTransitHours(Double estimatedTransitHours) { this.estimatedTransitHours = estimatedTransitHours; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getOriginAddress() { return originAddress; }
    public void setOriginAddress(String originAddress) { this.originAddress = originAddress; }

    public String getDestinationAddress() { return destinationAddress; }
    public void setDestinationAddress(String destinationAddress) { this.destinationAddress = destinationAddress; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedTransitTimeHours() { return estimatedTransitTimeHours; }
    public void setEstimatedTransitTimeHours(Double estimatedTransitTimeHours) { this.estimatedTransitTimeHours = estimatedTransitTimeHours; }

    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public DriverStatus getDriverStatus() { return driverStatus; }
    public void setDriverStatus(DriverStatus driverStatus) { this.driverStatus = driverStatus; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public Double getVehicleCapacity() { return vehicleCapacity; }
    public void setVehicleCapacity(Double vehicleCapacity) { this.vehicleCapacity = vehicleCapacity; }

    public String getVehicleCapacityUnit() { return vehicleCapacityUnit; }
    public void setVehicleCapacityUnit(String vehicleCapacityUnit) { this.vehicleCapacityUnit = vehicleCapacityUnit; }

    public long getActiveLoads() { return activeLoads; }
    public void setActiveLoads(long activeLoads) { this.activeLoads = activeLoads; }

    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }

    public Double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(Double originLatitude) { this.originLatitude = originLatitude; }

    public Double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(Double originLongitude) { this.originLongitude = originLongitude; }

    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Active shipment telemetry
    private String currentShipmentTrackingNumber;
    private String currentShipmentLocation;
    private Double currentShipmentLatitude;
    private Double currentShipmentLongitude;

    public String getCurrentShipmentTrackingNumber() { return currentShipmentTrackingNumber; }
    public void setCurrentShipmentTrackingNumber(String currentShipmentTrackingNumber) { this.currentShipmentTrackingNumber = currentShipmentTrackingNumber; }

    public String getCurrentShipmentLocation() { return currentShipmentLocation; }
    public void setCurrentShipmentLocation(String currentShipmentLocation) { this.currentShipmentLocation = currentShipmentLocation; }

    public Double getCurrentShipmentLatitude() { return currentShipmentLatitude; }
    public void setCurrentShipmentLatitude(Double currentShipmentLatitude) { this.currentShipmentLatitude = currentShipmentLatitude; }

    public Double getCurrentShipmentLongitude() { return currentShipmentLongitude; }
    public void setCurrentShipmentLongitude(Double currentShipmentLongitude) { this.currentShipmentLongitude = currentShipmentLongitude; }
}