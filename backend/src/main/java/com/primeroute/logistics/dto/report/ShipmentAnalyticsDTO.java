package com.primeroute.logistics.dto.report;

import java.util.List;
import java.util.Map;

public class ShipmentAnalyticsDTO {
    private long totalShipments;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> deliveryTypeDistribution;
    private List<ShipmentTrendPointReportDTO> trendSeries;

    public ShipmentAnalyticsDTO() {}

    public long getTotalShipments() { return totalShipments; }
    public void setTotalShipments(long totalShipments) { this.totalShipments = totalShipments; }
    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
    public Map<String, Long> getDeliveryTypeDistribution() { return deliveryTypeDistribution; }
    public void setDeliveryTypeDistribution(Map<String, Long> deliveryTypeDistribution) { this.deliveryTypeDistribution = deliveryTypeDistribution; }
    public List<ShipmentTrendPointReportDTO> getTrendSeries() { return trendSeries; }
    public void setTrendSeries(List<ShipmentTrendPointReportDTO> trendSeries) { this.trendSeries = trendSeries; }

    public static class ShipmentTrendPointReportDTO {
        private String date;
        private long delivered;
        private long inTransit;
        private long pending;
        private long cancelled;
        private long total;

        public ShipmentTrendPointReportDTO() {}
        public ShipmentTrendPointReportDTO(String date, long delivered, long inTransit, long pending, long cancelled, long total) {
            this.date = date;
            this.delivered = delivered;
            this.inTransit = inTransit;
            this.pending = pending;
            this.cancelled = cancelled;
            this.total = total;
        }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public long getDelivered() { return delivered; }
        public void setDelivered(long delivered) { this.delivered = delivered; }
        public long getInTransit() { return inTransit; }
        public void setInTransit(long inTransit) { this.inTransit = inTransit; }
        public long getPending() { return pending; }
        public void setPending(long pending) { this.pending = pending; }
        public long getCancelled() { return cancelled; }
        public void setCancelled(long cancelled) { this.cancelled = cancelled; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}
