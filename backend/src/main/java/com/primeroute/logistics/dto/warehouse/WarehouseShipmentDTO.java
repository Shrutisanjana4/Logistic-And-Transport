package com.primeroute.logistics.dto.warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.primeroute.logistics.dto.driver.ProofOfDeliveryDTO;

public class WarehouseShipmentDTO {

    private Long id;
    private String trackingNumber;
    private Long orderId;
    private String orderNumber;
    private String status;
    private String operationalStage;
    private String shipmentNumber;
    private String warehouseCode;
    private String warehouseName;
    private String assignmentStatus;
    private String assignedBy;
    private LocalDateTime assignedAt;

    // Customer details
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // Origin
    private Long originWarehouseId;
    private String originWarehouseName;
    private String originCity;
    private String originWarehouseAddress;
    private String pickupAddress;

    // Destination
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private String destinationCity;
    private String destinationWarehouseAddress;
    private String deliveryAddress;

    // Package
    private String packageDescription;
    private Double packageWeight;
    private Integer packageQuantity;
    private String deliveryType;
    private java.util.List<WarehouseTrackingEventDTO> trackingEvents;

    // Driver
    private Long driverId;
    private String driverName;
    private String driverPhone;
    private String driverStatus;

    // Vehicle
    private Long vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleRegistrationNumber;
    private String vehicleStatus;

    // Timing & Location
    private LocalDate estimatedDeliveryDate;
    private LocalDateTime receivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentLocationName;
    private LocalDateTime lastLocationUpdatedAt;
    // Items
    private java.util.List<com.primeroute.logistics.dto.OrderItemDTO> items;

    // Readiness
    private boolean readyForDispatch;
    private ProofOfDeliveryDTO proofOfDelivery;

    public WarehouseShipmentDTO() {}

    public String getShipmentNumber() { return shipmentNumber != null ? shipmentNumber : trackingNumber; }
    public void setShipmentNumber(String shipmentNumber) { this.shipmentNumber = shipmentNumber; }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getAssignmentStatus() { return assignmentStatus; }
    public void setAssignmentStatus(String assignmentStatus) { this.assignmentStatus = assignmentStatus; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperationalStage() { return operationalStage; }
    public void setOperationalStage(String operationalStage) { this.operationalStage = operationalStage; }

    public Long getOriginWarehouseId() { return originWarehouseId; }
    public void setOriginWarehouseId(Long originWarehouseId) { this.originWarehouseId = originWarehouseId; }

    public String getOriginWarehouseName() { return originWarehouseName; }
    public void setOriginWarehouseName(String originWarehouseName) { this.originWarehouseName = originWarehouseName; }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }

    public String getOriginWarehouseAddress() { return originWarehouseAddress; }
    public void setOriginWarehouseAddress(String originWarehouseAddress) { this.originWarehouseAddress = originWarehouseAddress; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public Long getDestinationWarehouseId() { return destinationWarehouseId; }
    public void setDestinationWarehouseId(Long destinationWarehouseId) { this.destinationWarehouseId = destinationWarehouseId; }

    public String getDestinationWarehouseName() { return destinationWarehouseName; }
    public void setDestinationWarehouseName(String destinationWarehouseName) { this.destinationWarehouseName = destinationWarehouseName; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public String getDestinationWarehouseAddress() { return destinationWarehouseAddress; }
    public void setDestinationWarehouseAddress(String destinationWarehouseAddress) { this.destinationWarehouseAddress = destinationWarehouseAddress; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getPackageDescription() { return packageDescription; }
    public void setPackageDescription(String packageDescription) { this.packageDescription = packageDescription; }

    public Double getPackageWeight() { return packageWeight; }
    public void setPackageWeight(Double packageWeight) { this.packageWeight = packageWeight; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public java.util.List<WarehouseTrackingEventDTO> getTrackingEvents() { return trackingEvents; }
    public void setTrackingEvents(java.util.List<WarehouseTrackingEventDTO> trackingEvents) { this.trackingEvents = trackingEvents; }

    public Integer getPackageQuantity() { return packageQuantity; }
    public void setPackageQuantity(Integer packageQuantity) { this.packageQuantity = packageQuantity; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = driverPhone; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }
    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) { this.vehicleRegistrationNumber = vehicleRegistrationNumber; }

    public String getVehicleStatus() { return vehicleStatus; }
    public void setVehicleStatus(String vehicleStatus) { this.vehicleStatus = vehicleStatus; }

    public LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getCurrentLatitude() { return currentLatitude; }
    public void setCurrentLatitude(Double currentLatitude) { this.currentLatitude = currentLatitude; }

    public Double getCurrentLongitude() { return currentLongitude; }
    public void setCurrentLongitude(Double currentLongitude) { this.currentLongitude = currentLongitude; }

    public String getCurrentLocationName() { return currentLocationName; }
    public void setCurrentLocationName(String currentLocationName) { this.currentLocationName = currentLocationName; }

    public ProofOfDeliveryDTO getProofOfDelivery() { return proofOfDelivery; }
    public void setProofOfDelivery(ProofOfDeliveryDTO proofOfDelivery) { this.proofOfDelivery = proofOfDelivery; }

    public boolean isReadyForDispatch() { return readyForDispatch; }
    public void setReadyForDispatch(boolean readyForDispatch) { this.readyForDispatch = readyForDispatch; }

    public java.util.List<com.primeroute.logistics.dto.OrderItemDTO> getItems() { return items; }
    public void setItems(java.util.List<com.primeroute.logistics.dto.OrderItemDTO> items) { this.items = items; }

    public LocalDateTime getLastLocationUpdatedAt() { return lastLocationUpdatedAt; }
    public void setLastLocationUpdatedAt(LocalDateTime lastLocationUpdatedAt) { this.lastLocationUpdatedAt = lastLocationUpdatedAt; }
}
