package com.primeroute.logistics.dto.report;

import java.util.List;

public class DriverPerformanceReportDTO {
    private long totalDrivers;
    private long activeDrivers;
    private long assignedDrivers;
    private long availableDrivers;
    private List<DriverPerformanceItemDTO> drivers;

    public DriverPerformanceReportDTO() {}

    public long getTotalDrivers() { return totalDrivers; }
    public void setTotalDrivers(long totalDrivers) { this.totalDrivers = totalDrivers; }
    public long getActiveDrivers() { return activeDrivers; }
    public void setActiveDrivers(long activeDrivers) { this.activeDrivers = activeDrivers; }
    public long getAssignedDrivers() { return assignedDrivers; }
    public void setAssignedDrivers(long assignedDrivers) { this.assignedDrivers = assignedDrivers; }
    public long getAvailableDrivers() { return availableDrivers; }
    public void setAvailableDrivers(long availableDrivers) { this.availableDrivers = availableDrivers; }
    public List<DriverPerformanceItemDTO> getDrivers() { return drivers; }
    public void setDrivers(List<DriverPerformanceItemDTO> drivers) { this.drivers = drivers; }

    public static class DriverPerformanceItemDTO {
        private Long id;
        private String name;
        private String licenseNumber;
        private String phone;
        private String status;
        private String assignedVehicle;
        private String assignedRoute;
        private long totalRuns;
        private long onTimeRuns;
        private double onTimeRate;
        private double rating;

        public DriverPerformanceItemDTO() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLicenseNumber() { return licenseNumber; }
        public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getAssignedVehicle() { return assignedVehicle; }
        public void setAssignedVehicle(String assignedVehicle) { this.assignedVehicle = assignedVehicle; }
        public String getAssignedRoute() { return assignedRoute; }
        public void setAssignedRoute(String assignedRoute) { this.assignedRoute = assignedRoute; }
        public long getTotalRuns() { return totalRuns; }
        public void setTotalRuns(long totalRuns) { this.totalRuns = totalRuns; }
        public long getOnTimeRuns() { return onTimeRuns; }
        public void setOnTimeRuns(long onTimeRuns) { this.onTimeRuns = onTimeRuns; }
        public double getOnTimeRate() { return onTimeRate; }
        public void setOnTimeRate(double onTimeRate) { this.onTimeRate = onTimeRate; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
    }
}
