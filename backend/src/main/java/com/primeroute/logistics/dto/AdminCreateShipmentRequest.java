package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotNull;

public class AdminCreateShipmentRequest {

    @NotNull(message = "Order ID is required to create a shipment")
    private Long orderId;

    @NotNull(message = "Fulfillment warehouse ID is required")
    private Long warehouseId;

    public AdminCreateShipmentRequest() {}

    public AdminCreateShipmentRequest(Long orderId, Long warehouseId) {
        this.orderId = orderId;
        this.warehouseId = warehouseId;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
}
