package com.primeroute.logistics.dto.report;

public class ComprehensiveReportDTO {
    private String dateRangeLabel;
    private String fromDate;
    private String toDate;
    private ReportOverviewDTO overview;
    private OrderAnalyticsDTO orders;
    private ShipmentAnalyticsDTO shipments;
    private DeliveryPerformanceDTO deliveryPerformance;
    private FleetAnalyticsDTO fleet;
    private DriverPerformanceReportDTO drivers;
    private RoutePerformanceReportDTO routes;
    private WarehouseAnalyticsReportDTO warehouses;
    private CustomerAnalyticsReportDTO customers;
    private FinancialAnalyticsReportDTO financial;

    public ComprehensiveReportDTO() {}

    public String getDateRangeLabel() { return dateRangeLabel; }
    public void setDateRangeLabel(String dateRangeLabel) { this.dateRangeLabel = dateRangeLabel; }
    public String getFromDate() { return fromDate; }
    public void setFromDate(String fromDate) { this.fromDate = fromDate; }
    public String getToDate() { return toDate; }
    public void setToDate(String toDate) { this.toDate = toDate; }
    public ReportOverviewDTO getOverview() { return overview; }
    public void setOverview(ReportOverviewDTO overview) { this.overview = overview; }
    public OrderAnalyticsDTO getOrders() { return orders; }
    public void setOrders(OrderAnalyticsDTO orders) { this.orders = orders; }
    public ShipmentAnalyticsDTO getShipments() { return shipments; }
    public void setShipments(ShipmentAnalyticsDTO shipments) { this.shipments = shipments; }
    public DeliveryPerformanceDTO getDeliveryPerformance() { return deliveryPerformance; }
    public void setDeliveryPerformance(DeliveryPerformanceDTO deliveryPerformance) { this.deliveryPerformance = deliveryPerformance; }
    public FleetAnalyticsDTO getFleet() { return fleet; }
    public void setFleet(FleetAnalyticsDTO fleet) { this.fleet = fleet; }
    public DriverPerformanceReportDTO getDrivers() { return drivers; }
    public void setDrivers(DriverPerformanceReportDTO drivers) { this.drivers = drivers; }
    public RoutePerformanceReportDTO getRoutes() { return routes; }
    public void setRoutes(RoutePerformanceReportDTO routes) { this.routes = routes; }
    public WarehouseAnalyticsReportDTO getWarehouses() { return warehouses; }
    public void setWarehouses(WarehouseAnalyticsReportDTO warehouses) { this.warehouses = warehouses; }
    public CustomerAnalyticsReportDTO getCustomers() { return customers; }
    public void setCustomers(CustomerAnalyticsReportDTO customers) { this.customers = customers; }
    public FinancialAnalyticsReportDTO getFinancial() { return financial; }
    public void setFinancial(FinancialAnalyticsReportDTO financial) { this.financial = financial; }
}
