package com.primeroute.logistics.dto;

public class AssignVehicleRequest {

    private Long vehicleId;

    public AssignVehicleRequest() {}

    public AssignVehicleRequest(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}