package com.primeroute.logistics.dto.warehouse;

public class AssignFleetRequest {
    private Long driverId;
    private Long vehicleId;

    public AssignFleetRequest() {}

    public AssignFleetRequest(Long driverId, Long vehicleId) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
