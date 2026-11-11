package com.primeroute.logistics.dto.report;

import java.util.List;

public class RoutePerformanceReportDTO {
    private long totalRoutes;
    private long activeRoutes;
    private long inactiveRoutes;
    private List<RoutePerformanceItemDTO> routes;

    public RoutePerformanceReportDTO() {}

    public long getTotalRoutes() { return totalRoutes; }
    public void setTotalRoutes(long totalRoutes) { this.totalRoutes = totalRoutes; }
    public long getActiveRoutes() { return activeRoutes; }
    public void setActiveRoutes(long activeRoutes) { this.activeRoutes = activeRoutes; }
    public long getInactiveRoutes() { return inactiveRoutes; }
    public void setInactiveRoutes(long inactiveRoutes) { this.inactiveRoutes = inactiveRoutes; }
    public List<RoutePerformanceItemDTO> getRoutes() { return routes; }
    public void setRoutes(List<RoutePerformanceItemDTO> routes) { this.routes = routes; }

    public static class RoutePerformanceItemDTO {
        private Long id;
        private String routeName;
        private String origin;
        private String destination;
        private double distanceKm;
        private double estimatedHours;
        private String assignedDriver;
        private String assignedVehicle;
        private long activeLoads;
        private long totalShipments;
        private String status;

        public RoutePerformanceItemDTO() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }
        public String getDestination() { return destination; }
        public void setDestination(String destination) { this.destination = destination; }
        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
        public double getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }
        public String getAssignedDriver() { return assignedDriver; }
        public void setAssignedDriver(String assignedDriver) { this.assignedDriver = assignedDriver; }
        public String getAssignedVehicle() { return assignedVehicle; }
        public void setAssignedVehicle(String assignedVehicle) { this.assignedVehicle = assignedVehicle; }
        public long getActiveLoads() { return activeLoads; }
        public void setActiveLoads(long activeLoads) { this.activeLoads = activeLoads; }
        public long getTotalShipments() { return totalShipments; }
        public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
