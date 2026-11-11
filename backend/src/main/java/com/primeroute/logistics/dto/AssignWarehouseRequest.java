package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotNull;

public class AssignWarehouseRequest {

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    public AssignWarehouseRequest() {}

    public AssignWarehouseRequest(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }
}
