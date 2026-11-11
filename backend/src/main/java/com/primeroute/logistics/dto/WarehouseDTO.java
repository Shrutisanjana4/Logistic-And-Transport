package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.WarehouseStatus;
import com.primeroute.logistics.entity.WarehouseType;
import java.time.LocalDateTime;

public class WarehouseDTO {
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private WarehouseType warehouseType;
    private WarehouseStatus status;
    private String managerName;
    private String managerPhone;
    private String managerEmail;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
    private Double totalCapacity;
    private Double occupiedCapacity;
    private Double availableCapacity;
    private Double utilizationRate;
    private String capacityUnit;
    private String operatingHours;
    private String contactPhone;
    private String contactEmail;
    private Long activeShipmentCount = 0L;
    private Long activeRouteCount = 0L;
    private Long inboundLoadCount = 0L;
    private Long outboundLoadCount = 0L;
    private Long availableDriverCount = 0L;
    private Long availableVehicleCount = 0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WarehouseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public WarehouseType getWarehouseType() { return warehouseType; }
    public void setWarehouseType(WarehouseType warehouseType) { this.warehouseType = warehouseType; }

    public WarehouseStatus getStatus() { return status; }
    public void setStatus(WarehouseStatus status) { this.status = status; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerPhone() { return managerPhone; }
    public void setManagerPhone(String managerPhone) { this.managerPhone = managerPhone; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }

    public Double getOccupiedCapacity() { return occupiedCapacity; }
    public void setOccupiedCapacity(Double occupiedCapacity) { this.occupiedCapacity = occupiedCapacity; }

    public Double getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; }

    public Double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(Double utilizationRate) { this.utilizationRate = utilizationRate; }

    public String getCapacityUnit() { return capacityUnit; }
    public void setCapacityUnit(String capacityUnit) { this.capacityUnit = capacityUnit; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Long getActiveShipmentCount() { return activeShipmentCount; }
    public void setActiveShipmentCount(Long activeShipmentCount) { this.activeShipmentCount = activeShipmentCount; }

    public Long getActiveRouteCount() { return activeRouteCount; }
    public void setActiveRouteCount(Long activeRouteCount) { this.activeRouteCount = activeRouteCount; }

    public Long getInboundLoadCount() { return inboundLoadCount; }
    public void setInboundLoadCount(Long inboundLoadCount) { this.inboundLoadCount = inboundLoadCount; }

    public Long getOutboundLoadCount() { return outboundLoadCount; }
    public void setOutboundLoadCount(Long outboundLoadCount) { this.outboundLoadCount = outboundLoadCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getAvailableDriverCount() { return availableDriverCount; }
    public void setAvailableDriverCount(Long availableDriverCount) { this.availableDriverCount = availableDriverCount; }

    public Long getAvailableVehicleCount() { return availableVehicleCount; }
    public void setAvailableVehicleCount(Long availableVehicleCount) { this.availableVehicleCount = availableVehicleCount; }
}
