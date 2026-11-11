package com.primeroute.logistics.dto.report;

import java.util.List;
import java.util.Map;

public class FleetAnalyticsDTO {
    private long totalVehicles;
    private long activeVehicles;
    private long availableVehicles;
    private long assignedVehicles;
    private long maintenanceVehicles;
    private double utilizationRate;
    private Map<String, Long> vehicleTypeDistribution;
    private List<VehicleRosterItemDTO> vehicleRoster;

    public FleetAnalyticsDTO() {}

    public long getTotalVehicles() { return totalVehicles; }
    public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
    public long getActiveVehicles() { return activeVehicles; }
    public void setActiveVehicles(long activeVehicles) { this.activeVehicles = activeVehicles; }
    public long getAvailableVehicles() { return availableVehicles; }
    public void setAvailableVehicles(long availableVehicles) { this.availableVehicles = availableVehicles; }
    public long getAssignedVehicles() { return assignedVehicles; }
    public void setAssignedVehicles(long assignedVehicles) { this.assignedVehicles = assignedVehicles; }
    public long getMaintenanceVehicles() { return maintenanceVehicles; }
    public void setMaintenanceVehicles(long maintenanceVehicles) { this.maintenanceVehicles = maintenanceVehicles; }
    public double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(double utilizationRate) { this.utilizationRate = utilizationRate; }
    public Map<String, Long> getVehicleTypeDistribution() { return vehicleTypeDistribution; }
    public void setVehicleTypeDistribution(Map<String, Long> vehicleTypeDistribution) { this.vehicleTypeDistribution = vehicleTypeDistribution; }
    public List<VehicleRosterItemDTO> getVehicleRoster() { return vehicleRoster; }
    public void setVehicleRoster(List<VehicleRosterItemDTO> vehicleRoster) { this.vehicleRoster = vehicleRoster; }

    public static class VehicleRosterItemDTO {
        private Long id;
        private String licensePlate;
        private String model;
        private String type;
        private String status;
        private double capacityTons;
        private String assignedDriver;
        private String assignedRoute;
        private long activeLoads;

        public VehicleRosterItemDTO() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getLicensePlate() { return licensePlate; }
        public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getCapacityTons() { return capacityTons; }
        public void setCapacityTons(double capacityTons) { this.capacityTons = capacityTons; }
        public String getAssignedDriver() { return assignedDriver; }
        public void setAssignedDriver(String assignedDriver) { this.assignedDriver = assignedDriver; }
        public String getAssignedRoute() { return assignedRoute; }
        public void setAssignedRoute(String assignedRoute) { this.assignedRoute = assignedRoute; }
        public long getActiveLoads() { return activeLoads; }
        public void setActiveLoads(long activeLoads) { this.activeLoads = activeLoads; }
    }
}
