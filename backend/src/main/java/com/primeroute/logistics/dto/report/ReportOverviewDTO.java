package com.primeroute.logistics.dto.report;

public class ReportOverviewDTO {
    private long totalOrders;
    private long totalShipments;
    private long activeShipments;
    private long deliveredShipments;
    private long pendingOrders;
    private long inTransitShipments;
    private long totalCustomers;
    private long activeDrivers;
    private long activeVehicles;
    private long activeRoutes;
    private long warehouseCount;
    private double totalInvoiceValue;
    private double outstandingAmount;
    private double totalPaidAmount;

    public ReportOverviewDTO() {}

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
    public long getActiveShipments() { return activeShipments; }
    public void setActiveShipments(long activeShipments) { this.activeShipments = activeShipments; }
    public long getDeliveredShipments() { return deliveredShipments; }
    public void setDeliveredShipments(long deliveredShipments) { this.deliveredShipments = deliveredShipments; }
    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    public long getInTransitShipments() { return inTransitShipments; }
    public void setInTransitShipments(long inTransitShipments) { this.inTransitShipments = inTransitShipments; }
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getActiveDrivers() { return activeDrivers; }
    public void setActiveDrivers(long activeDrivers) { this.activeDrivers = activeDrivers; }
    public long getActiveVehicles() { return activeVehicles; }
    public void setActiveVehicles(long activeVehicles) { this.activeVehicles = activeVehicles; }
    public long getActiveRoutes() { return activeRoutes; }
    public void setActiveRoutes(long activeRoutes) { this.activeRoutes = activeRoutes; }
    public long getWarehouseCount() { return warehouseCount; }
    public void setWarehouseCount(long warehouseCount) { this.warehouseCount = warehouseCount; }
    public double getTotalInvoiceValue() { return totalInvoiceValue; }
    public void setTotalInvoiceValue(double totalInvoiceValue) { this.totalInvoiceValue = totalInvoiceValue; }
    public double getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(double outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    public double getTotalPaidAmount() { return totalPaidAmount; }
    public void setTotalPaidAmount(double totalPaidAmount) { this.totalPaidAmount = totalPaidAmount; }
}
