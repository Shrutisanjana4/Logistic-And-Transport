package com.primeroute.logistics.dto.report;

import java.util.List;

public class WarehouseAnalyticsReportDTO {
    private long totalWarehouses;
    private long activeWarehouses;
    private long totalCapacity;
    private long occupiedCapacity;
    private double overallUtilizationRate;
    private List<WarehouseReportItemDTO> warehouses;

    public WarehouseAnalyticsReportDTO() {}

    public long getTotalWarehouses() { return totalWarehouses; }
    public void setTotalWarehouses(long totalWarehouses) { this.totalWarehouses = totalWarehouses; }
    public long getActiveWarehouses() { return activeWarehouses; }
    public void setActiveWarehouses(long activeWarehouses) { this.activeWarehouses = activeWarehouses; }
    public long getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(long totalCapacity) { this.totalCapacity = totalCapacity; }
    public long getOccupiedCapacity() { return occupiedCapacity; }
    public void setOccupiedCapacity(long occupiedCapacity) { this.occupiedCapacity = occupiedCapacity; }
    public double getOverallUtilizationRate() { return overallUtilizationRate; }
    public void setOverallUtilizationRate(double overallUtilizationRate) { this.overallUtilizationRate = overallUtilizationRate; }
    public List<WarehouseReportItemDTO> getWarehouses() { return warehouses; }
    public void setWarehouses(List<WarehouseReportItemDTO> warehouses) { this.warehouses = warehouses; }

    public static class WarehouseReportItemDTO {
        private Long id;
        private String code;
        private String name;
        private String location;
        private String manager;
        private long totalCapacity;
        private long usedCapacity;
        private double utilizationRate;
        private String status;
        private String type;

        public WarehouseReportItemDTO() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getManager() { return manager; }
        public void setManager(String manager) { this.manager = manager; }
        public long getTotalCapacity() { return totalCapacity; }
        public void setTotalCapacity(long totalCapacity) { this.totalCapacity = totalCapacity; }
        public long getUsedCapacity() { return usedCapacity; }
        public void setUsedCapacity(long usedCapacity) { this.usedCapacity = usedCapacity; }
        public double getUtilizationRate() { return utilizationRate; }
        public void setUtilizationRate(double utilizationRate) { this.utilizationRate = utilizationRate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
