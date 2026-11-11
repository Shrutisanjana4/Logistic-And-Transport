package com.primeroute.logistics.dto.driver;

public class DriverProfileDTO {
    private Long id;
    private String driverCode;
    private String driverId;
    private String fullName;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String licenseExpiry;
    private String address;
    private String status;
    private Long assignedWarehouseId;
    private String assignedWarehouseCode;
    private String assignedWarehouseName;
    private String assignedHub;
    private Long assignedVehicleId;
    private String assignedVehicleCode;
    private String assignedVehiclePlate;
    private String assignedVehicleModel;
    private String assignedVehicleType;
    private int totalTripsCompleted;
    private int totalDeliveriesCompleted;
    private double onTimeRatePercentage;
    private double safetyRating;
    private String shiftStartedAt;

    public DriverProfileDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDriverCode() { return driverCode; }
    public void setDriverCode(String driverCode) { this.driverCode = driverCode; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getLicenseExpiry() { return licenseExpiry; }
    public void setLicenseExpiry(String licenseExpiry) { this.licenseExpiry = licenseExpiry; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getAssignedWarehouseId() { return assignedWarehouseId; }
    public void setAssignedWarehouseId(Long assignedWarehouseId) { this.assignedWarehouseId = assignedWarehouseId; }

    public String getAssignedWarehouseCode() { return assignedWarehouseCode; }
    public void setAssignedWarehouseCode(String assignedWarehouseCode) { this.assignedWarehouseCode = assignedWarehouseCode; }

    public String getAssignedWarehouseName() { return assignedWarehouseName; }
    public void setAssignedWarehouseName(String assignedWarehouseName) { this.assignedWarehouseName = assignedWarehouseName; }

    public String getAssignedHub() { return assignedHub; }
    public void setAssignedHub(String assignedHub) { this.assignedHub = assignedHub; }

    public Long getAssignedVehicleId() { return assignedVehicleId; }
    public void setAssignedVehicleId(Long assignedVehicleId) { this.assignedVehicleId = assignedVehicleId; }

    public String getAssignedVehicleCode() { return assignedVehicleCode; }
    public void setAssignedVehicleCode(String assignedVehicleCode) { this.assignedVehicleCode = assignedVehicleCode; }

    public String getAssignedVehiclePlate() { return assignedVehiclePlate; }
    public void setAssignedVehiclePlate(String assignedVehiclePlate) { this.assignedVehiclePlate = assignedVehiclePlate; }

    public String getAssignedVehicleModel() { return assignedVehicleModel; }
    public void setAssignedVehicleModel(String assignedVehicleModel) { this.assignedVehicleModel = assignedVehicleModel; }

    public String getAssignedVehicleType() { return assignedVehicleType; }
    public void setAssignedVehicleType(String assignedVehicleType) { this.assignedVehicleType = assignedVehicleType; }

    public int getTotalTripsCompleted() { return totalTripsCompleted; }
    public void setTotalTripsCompleted(int totalTripsCompleted) { this.totalTripsCompleted = totalTripsCompleted; }

    public int getTotalDeliveriesCompleted() { return totalDeliveriesCompleted; }
    public void setTotalDeliveriesCompleted(int totalDeliveriesCompleted) { this.totalDeliveriesCompleted = totalDeliveriesCompleted; }

    public double getOnTimeRatePercentage() { return onTimeRatePercentage; }
    public void setOnTimeRatePercentage(double onTimeRatePercentage) { this.onTimeRatePercentage = onTimeRatePercentage; }

    public double getSafetyRating() { return safetyRating; }
    public void setSafetyRating(double safetyRating) { this.safetyRating = safetyRating; }

    public String getShiftStartedAt() { return shiftStartedAt; }
    public void setShiftStartedAt(String shiftStartedAt) { this.shiftStartedAt = shiftStartedAt; }
}
