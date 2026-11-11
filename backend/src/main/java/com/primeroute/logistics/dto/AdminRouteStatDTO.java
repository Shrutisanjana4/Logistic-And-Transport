package com.primeroute.logistics.dto;

public class AdminRouteStatDTO {
    private String route;
    private long shipments;

    public AdminRouteStatDTO() {}
    public AdminRouteStatDTO(String route, long shipments) {
        this.route = route;
        this.shipments = shipments;
    }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public long getShipments() { return shipments; }
    public void setShipments(long shipments) { this.shipments = shipments; }
}
