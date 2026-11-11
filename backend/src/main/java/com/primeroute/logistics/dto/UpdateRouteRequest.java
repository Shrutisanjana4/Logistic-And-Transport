package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.RouteStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateRouteRequest {

    private String routeCode;
    private String routeName;

    @NotBlank(message = "Origin is required")
    private String origin;

    private String originName;
    private String originCity;
    private String originState;

    @NotBlank(message = "Destination is required")
    private String destination;

    private String destinationName;
    private String destinationCity;
    private String destinationState;

    private String originAddress;
    private String destinationAddress;

    @NotNull(message = "Distance in km is required")
    @Positive(message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Estimated transit time in hours is required")
    @Positive(message = "Estimated transit time must be greater than 0")
    private Double estimatedTransitTimeHours;
    private Double estimatedTransitHours;
    private Double distance;

    private Long driverId;
    private Long vehicleId;

    private Double originLatitude;
    private Double originLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    private RouteStatus status;

    public UpdateRouteRequest() {}

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getRouteName() { return routeName; }
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

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public Double getOriginLatitude() { return originLatitude; }
    public void setOriginLatitude(Double originLatitude) { this.originLatitude = originLatitude; }

    public Double getOriginLongitude() { return originLongitude; }
    public void setOriginLongitude(Double originLongitude) { this.originLongitude = originLongitude; }

    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }

    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }

    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }
}