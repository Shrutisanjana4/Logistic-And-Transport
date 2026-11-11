package com.primeroute.logistics.dto.warehouse;

import java.util.List;
import java.util.Map;

public class WarehouseReportDTO {

    private String warehouseCode;
    private String warehouseName;
    private String dateRange;

    // Aggregated Metrics
    private long totalItems;
    private long totalStockQuantity;
    private double stockValue;
    private long totalDispatches;
    private long activeShipments;
    private double averageUtilization;

    // Charts data
    private List<Map<String, Object>> stockByCategory;
    private List<Map<String, Object>> dailyDispatches;
    private List<Map<String, Object>> fleetUtilization;
    private List<Map<String, Object>> transactionBreakdown;

    public WarehouseReportDTO() {}

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getDateRange() { return dateRange; }
    public void setDateRange(String dateRange) { this.dateRange = dateRange; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public long getTotalStockQuantity() { return totalStockQuantity; }
    public void setTotalStockQuantity(long totalStockQuantity) { this.totalStockQuantity = totalStockQuantity; }

    public double getStockValue() { return stockValue; }
    public void setStockValue(double stockValue) { this.stockValue = stockValue; }

    public long getTotalDispatches() { return totalDispatches; }
    public void setTotalDispatches(long totalDispatches) { this.totalDispatches = totalDispatches; }

    public long getActiveShipments() { return activeShipments; }
    public void setActiveShipments(long activeShipments) { this.activeShipments = activeShipments; }

    public double getAverageUtilization() { return averageUtilization; }
    public void setAverageUtilization(double averageUtilization) { this.averageUtilization = averageUtilization; }

    public List<Map<String, Object>> getStockByCategory() { return stockByCategory; }
    public void setStockByCategory(List<Map<String, Object>> stockByCategory) { this.stockByCategory = stockByCategory; }

    public List<Map<String, Object>> getDailyDispatches() { return dailyDispatches; }
    public void setDailyDispatches(List<Map<String, Object>> dailyDispatches) { this.dailyDispatches = dailyDispatches; }

    public List<Map<String, Object>> getFleetUtilization() { return fleetUtilization; }
    public void setFleetUtilization(List<Map<String, Object>> fleetUtilization) { this.fleetUtilization = fleetUtilization; }

    public List<Map<String, Object>> getTransactionBreakdown() { return transactionBreakdown; }
    public void setTransactionBreakdown(List<Map<String, Object>> transactionBreakdown) { this.transactionBreakdown = transactionBreakdown; }
}
