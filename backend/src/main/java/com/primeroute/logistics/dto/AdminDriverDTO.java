package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.DriverStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminDriverDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private String address;
    private Long warehouseId;
    private String warehouseName;
    private Long vehicleId;
    private String vehicleNumber;
    private String currentShipmentTrackingNumber;
    private DriverStatus status;
    private Integer assignedShipmentsCount;
    private List<String> assignedShipmentNumbers = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminDriverDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public Integer getAssignedShipmentsCount() { return assignedShipmentsCount; }
    public void setAssignedShipmentsCount(Integer assignedShipmentsCount) { this.assignedShipmentsCount = assignedShipmentsCount; }

    public List<String> getAssignedShipmentNumbers() { return assignedShipmentNumbers; }
    public void setAssignedShipmentNumbers(List<String> assignedShipmentNumbers) { this.assignedShipmentNumbers = assignedShipmentNumbers; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getCurrentShipmentTrackingNumber() { return currentShipmentTrackingNumber; }
    public void setCurrentShipmentTrackingNumber(String currentShipmentTrackingNumber) { this.currentShipmentTrackingNumber = currentShipmentTrackingNumber; }
}
