package com.primeroute.logistics.dto.warehouse;

import java.util.List;

public class WarehouseDashboardDTO {

    // Warehouse info
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String warehouseType;
    private String managerName;
    private String managerEmail;
    private String city;
    private String state;
    private Double totalCapacity;
    private Double occupiedCapacity;
    private Double availableCapacity;
    private Double utilizationRate;
    private String status;
    private String operatingHours;

    // Inventory KPIs
    private long totalInventoryItems;
    private long availableStock;
    private long reservedStock;
    private long damagedStock;
    private long lowStockCount;

    // Shipment KPIs
    private long incomingShipmentsCount;
    private long outgoingShipmentsCount;
    private long readyToProcessCount;
    private long pickingCount;
    private long packedCount;
    private long readyForDispatchCount;

    // Fleet KPIs
    private long driversAvailableCount;
    private long driversTotalCount;
    private long vehiclesAvailableCount;
    private long vehiclesTotalCount;

    // Previews / Recent Activity
    private List<WarehouseActivityDTO> recentActivities;
    private List<WarehouseInventoryDTO> lowStockAlerts;
    private List<WarehouseShipmentDTO> incomingPreview;
    private List<WarehouseShipmentDTO> outgoingPreview;
    private List<FulfillmentAlertDTO> fulfillmentAlerts;

    public WarehouseDashboardDTO() {}

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getWarehouseType() { return warehouseType; }
    public void setWarehouseType(String warehouseType) { this.warehouseType = warehouseType; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerEmail() { return managerEmail; }
    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Double totalCapacity) { this.totalCapacity = totalCapacity; }

    public Double getOccupiedCapacity() { return occupiedCapacity; }
    public void setOccupiedCapacity(Double occupiedCapacity) { this.occupiedCapacity = occupiedCapacity; }

    public Double getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(Double availableCapacity) { this.availableCapacity = availableCapacity; }

    public Double getUtilizationRate() { return utilizationRate; }
    public void setUtilizationRate(Double utilizationRate) { this.utilizationRate = utilizationRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public long getTotalInventoryItems() { return totalInventoryItems; }
    public void setTotalInventoryItems(long totalInventoryItems) { this.totalInventoryItems = totalInventoryItems; }

    public long getAvailableStock() { return availableStock; }
    public void setAvailableStock(long availableStock) { this.availableStock = availableStock; }

    public long getReservedStock() { return reservedStock; }
    public void setReservedStock(long reservedStock) { this.reservedStock = reservedStock; }

    public long getDamagedStock() { return damagedStock; }
    public void setDamagedStock(long damagedStock) { this.damagedStock = damagedStock; }

    public long getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(long lowStockCount) { this.lowStockCount = lowStockCount; }

    public long getIncomingShipmentsCount() { return incomingShipmentsCount; }
    public void setIncomingShipmentsCount(long incomingShipmentsCount) { this.incomingShipmentsCount = incomingShipmentsCount; }

    public long getOutgoingShipmentsCount() { return outgoingShipmentsCount; }
    public void setOutgoingShipmentsCount(long outgoingShipmentsCount) { this.outgoingShipmentsCount = outgoingShipmentsCount; }

    public long getReadyToProcessCount() { return readyToProcessCount; }
    public void setReadyToProcessCount(long readyToProcessCount) { this.readyToProcessCount = readyToProcessCount; }

    public long getPickingCount() { return pickingCount; }
    public void setPickingCount(long pickingCount) { this.pickingCount = pickingCount; }

    public long getPackedCount() { return packedCount; }
    public void setPackedCount(long packedCount) { this.packedCount = packedCount; }

    public long getReadyForDispatchCount() { return readyForDispatchCount; }
    public void setReadyForDispatchCount(long readyForDispatchCount) { this.readyForDispatchCount = readyForDispatchCount; }

    public long getDriversAvailableCount() { return driversAvailableCount; }
    public void setDriversAvailableCount(long driversAvailableCount) { this.driversAvailableCount = driversAvailableCount; }

    public long getDriversTotalCount() { return driversTotalCount; }
    public void setDriversTotalCount(long driversTotalCount) { this.driversTotalCount = driversTotalCount; }

    public long getVehiclesAvailableCount() { return vehiclesAvailableCount; }
    public void setVehiclesAvailableCount(long vehiclesAvailableCount) { this.vehiclesAvailableCount = vehiclesAvailableCount; }

    public long getVehiclesTotalCount() { return vehiclesTotalCount; }
    public void setVehiclesTotalCount(long vehiclesTotalCount) { this.vehiclesTotalCount = vehiclesTotalCount; }

    public List<WarehouseActivityDTO> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<WarehouseActivityDTO> recentActivities) { this.recentActivities = recentActivities; }

    public List<WarehouseInventoryDTO> getLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(List<WarehouseInventoryDTO> lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }

    public List<WarehouseShipmentDTO> getIncomingPreview() { return incomingPreview; }
    public void setIncomingPreview(List<WarehouseShipmentDTO> incomingPreview) { this.incomingPreview = incomingPreview; }

    public List<WarehouseShipmentDTO> getOutgoingPreview() { return outgoingPreview; }
    public void setOutgoingPreview(List<WarehouseShipmentDTO> outgoingPreview) { this.outgoingPreview = outgoingPreview; }
    public List<FulfillmentAlertDTO> getFulfillmentAlerts() { return fulfillmentAlerts; }
    public void setFulfillmentAlerts(List<FulfillmentAlertDTO> fulfillmentAlerts) { this.fulfillmentAlerts = fulfillmentAlerts; }
}