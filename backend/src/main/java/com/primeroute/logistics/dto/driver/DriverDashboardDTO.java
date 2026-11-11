package com.primeroute.logistics.dto.driver;

import java.util.ArrayList;
import java.util.List;

public class DriverDashboardDTO {
    private String driverName;
    private String driverId;
    private String driverCode;
    private String driverStatus;
    private DriverProfileDTO profile;
    private DriverVehicleDTO assignedVehicle;
    private int todaysDeliveries;
    private int assignedToday;
    private int completedDeliveries;
    private int completedToday;
    private int pendingDeliveries;
    private int pendingToday;
    private Double distanceKmToday;
    private Double distanceCoveredTodayKm;
    private double onTimeDeliveries;
    private DriverDeliveryDTO activeDelivery;
    private List<DriverDeliveryDTO> recentDeliveries = new ArrayList<>();

    public DriverDashboardDTO() {}

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getDriverCode() { return driverCode; }
    public void setDriverCode(String driverCode) { this.driverCode = driverCode; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public DriverProfileDTO getProfile() { return profile; }
    public void setProfile(DriverProfileDTO profile) { this.profile = profile; }

    public DriverVehicleDTO getAssignedVehicle() { return assignedVehicle; }
    public void setAssignedVehicle(DriverVehicleDTO assignedVehicle) { this.assignedVehicle = assignedVehicle; }

    public int getTodaysDeliveries() { return todaysDeliveries; }
    public void setTodaysDeliveries(int todaysDeliveries) { this.todaysDeliveries = todaysDeliveries; }

    public int getAssignedToday() { return assignedToday; }
    public void setAssignedToday(int assignedToday) { this.assignedToday = assignedToday; }

    public int getCompletedDeliveries() { return completedDeliveries; }
    public void setCompletedDeliveries(int completedDeliveries) { this.completedDeliveries = completedDeliveries; }

    public int getCompletedToday() { return completedToday; }
    public void setCompletedToday(int completedToday) { this.completedToday = completedToday; }

    public int getPendingDeliveries() { return pendingDeliveries; }
    public void setPendingDeliveries(int pendingDeliveries) { this.pendingDeliveries = pendingDeliveries; }

    public int getPendingToday() { return pendingToday; }
    public void setPendingToday(int pendingToday) { this.pendingToday = pendingToday; }

    public Double getDistanceKmToday() { return distanceKmToday; }
    public void setDistanceKmToday(Double distanceKmToday) { this.distanceKmToday = distanceKmToday; }

    public Double getDistanceCoveredTodayKm() { return distanceCoveredTodayKm; }
    public void setDistanceCoveredTodayKm(Double distanceCoveredTodayKm) { this.distanceCoveredTodayKm = distanceCoveredTodayKm; }

    public double getOnTimeDeliveries() { return onTimeDeliveries; }
    public void setOnTimeDeliveries(double onTimeDeliveries) { this.onTimeDeliveries = onTimeDeliveries; }

    public DriverDeliveryDTO getActiveDelivery() { return activeDelivery; }
    public void setActiveDelivery(DriverDeliveryDTO activeDelivery) { this.activeDelivery = activeDelivery; }

    public List<DriverDeliveryDTO> getRecentDeliveries() { return recentDeliveries; }
    public void setRecentDeliveries(List<DriverDeliveryDTO> recentDeliveries) { this.recentDeliveries = recentDeliveries; }
}
