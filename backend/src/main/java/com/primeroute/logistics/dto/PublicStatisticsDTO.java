package com.primeroute.logistics.dto;

public class PublicStatisticsDTO {
    private long activeCustomers;
    private long shipmentsManaged;
    private long deliveredShipments;
    private long inTransitShipments;
    private String supportAvailability;

    public PublicStatisticsDTO() {}

    public PublicStatisticsDTO(long activeCustomers, long shipmentsManaged, long deliveredShipments, long inTransitShipments, String supportAvailability) {
        this.activeCustomers = activeCustomers;
        this.shipmentsManaged = shipmentsManaged;
        this.deliveredShipments = deliveredShipments;
        this.inTransitShipments = inTransitShipments;
        this.supportAvailability = supportAvailability;
    }

    public long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(long activeCustomers) { this.activeCustomers = activeCustomers; }

    public long getShipmentsManaged() { return shipmentsManaged; }
    public void setShipmentsManaged(long shipmentsManaged) { this.shipmentsManaged = shipmentsManaged; }

    public long getDeliveredShipments() { return deliveredShipments; }
    public void setDeliveredShipments(long deliveredShipments) { this.deliveredShipments = deliveredShipments; }

    public long getInTransitShipments() { return inTransitShipments; }
    public void setInTransitShipments(long inTransitShipments) { this.inTransitShipments = inTransitShipments; }

    public String getSupportAvailability() { return supportAvailability; }
    public void setSupportAvailability(String supportAvailability) { this.supportAvailability = supportAvailability; }
}
