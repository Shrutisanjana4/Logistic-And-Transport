package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.VehicleStatus;
import com.primeroute.logistics.entity.VehicleType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VehicleDTO {
    private Long id;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String manufacturer;
    private String model;
    private Double capacity;
    private String capacityUnit;
    private String registrationNumber;
    private LocalDate registrationExpiry;
    private LocalDate insuranceExpiry;
    private VehicleStatus status;
    private String currentLocation;
    private Long warehouseId;
    private String warehouseName;

    // Driver telemetry
    private Long driverId;
    private String driverName;
    private String driverPhone;

    // Current shipment telemetry
    private Long currentShipmentId;
    private String currentShipmentTrackingNumber;
    private String currentShipmentOrderNumber;
    private String currentShipmentStatus;
    private String currentShipmentOrigin;
    private String currentShipmentDestination;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }

    public String getCapacityUnit() { return capacityUnit; }
    public void setCapacityUnit(String capacityUnit) { this.capacityUnit = capacityUnit; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public LocalDate getRegistrationExpiry() { return registrationExpiry; }
    public void setRegistrationExpiry(LocalDate registrationExpiry) { this.registrationExpiry = registrationExpiry; }

    public LocalDate getInsuranceExpiry() { return insuranceExpiry; }
    public void setInsuranceExpiry(LocalDate insuranceExpiry) { this.insuranceExpiry = insuranceExpiry; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public Long getCurrentShipmentId() { return currentShipmentId; }
    public void setCurrentShipmentId(Long currentShipmentId) { this.currentShipmentId = currentShipmentId; }

    public String getCurrentShipmentTrackingNumber() { return currentShipmentTrackingNumber; }
    public void setCurrentShipmentTrackingNumber(String currentShipmentTrackingNumber) { this.currentShipmentTrackingNumber = currentShipmentTrackingNumber; }

    public String getCurrentShipmentOrderNumber() { return currentShipmentOrderNumber; }
    public void setCurrentShipmentOrderNumber(String currentShipmentOrderNumber) { this.currentShipmentOrderNumber = currentShipmentOrderNumber; }

    public String getCurrentShipmentStatus() { return currentShipmentStatus; }
    public void setCurrentShipmentStatus(String currentShipmentStatus) { this.currentShipmentStatus = currentShipmentStatus; }

    public String getCurrentShipmentOrigin() { return currentShipmentOrigin; }
    public void setCurrentShipmentOrigin(String currentShipmentOrigin) { this.currentShipmentOrigin = currentShipmentOrigin; }

    public String getCurrentShipmentDestination() { return currentShipmentDestination; }
    public void setCurrentShipmentDestination(String currentShipmentDestination) { this.currentShipmentDestination = currentShipmentDestination; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
}
