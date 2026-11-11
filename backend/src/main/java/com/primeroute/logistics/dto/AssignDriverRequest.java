package com.primeroute.logistics.dto;

public class AssignDriverRequest {
    private Long driverId;

    public AssignDriverRequest() {}
    public AssignDriverRequest(Long driverId) { this.driverId = driverId; }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
}