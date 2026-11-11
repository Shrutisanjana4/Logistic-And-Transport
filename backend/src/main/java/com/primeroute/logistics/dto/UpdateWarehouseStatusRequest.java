package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.WarehouseStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateWarehouseStatusRequest {

    @NotNull(message = "Status cannot be null")
    private WarehouseStatus status;

    public UpdateWarehouseStatusRequest() {}

    public WarehouseStatus getStatus() { return status; }
    public void setStatus(WarehouseStatus status) { this.status = status; }
}
