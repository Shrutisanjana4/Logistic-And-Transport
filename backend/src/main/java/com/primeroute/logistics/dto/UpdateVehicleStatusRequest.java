package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.VehicleStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateVehicleStatusRequest {
    @NotNull(message = "Status is required")
    private VehicleStatus status;

    public UpdateVehicleStatusRequest() {}
    public UpdateVehicleStatusRequest(VehicleStatus status) { this.status = status; }

    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
}