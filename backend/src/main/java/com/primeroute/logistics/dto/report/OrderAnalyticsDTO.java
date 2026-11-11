package com.primeroute.logistics.dto.report;

import java.util.List;
import java.util.Map;

public class OrderAnalyticsDTO {
    private long totalOrders;
    private Map<String, Long> statusDistribution;
    private Map<String, Long> deliveryTypeDistribution;
    private double totalBookingVolume;
    private double averageOrderValue;
    private double fulfillmentRate;
    private List<OrderTrendPointDTO> trendSeries;

    public OrderAnalyticsDTO() {}

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }
    public Map<String, Long> getDeliveryTypeDistribution() { return deliveryTypeDistribution; }
    public void setDeliveryTypeDistribution(Map<String, Long> deliveryTypeDistribution) { this.deliveryTypeDistribution = deliveryTypeDistribution; }
    public double getTotalBookingVolume() { return totalBookingVolume; }
    public void setTotalBookingVolume(double totalBookingVolume) { this.totalBookingVolume = totalBookingVolume; }
    public double getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    public double getFulfillmentRate() { return fulfillmentRate; }
    public void setFulfillmentRate(double fulfillmentRate) { this.fulfillmentRate = fulfillmentRate; }
    public List<OrderTrendPointDTO> getTrendSeries() { return trendSeries; }
    public void setTrendSeries(List<OrderTrendPointDTO> trendSeries) { this.trendSeries = trendSeries; }

    public static class OrderTrendPointDTO {
        private String date;
        private long orders;
        private double volume;

        public OrderTrendPointDTO() {}
        public OrderTrendPointDTO(String date, long orders, double volume) {
            this.date = date;
            this.orders = orders;
            this.volume = volume;
        }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public long getOrders() { return orders; }
        public void setOrders(long orders) { this.orders = orders; }
        public double getVolume() { return volume; }
        public void setVolume(double volume) { this.volume = volume; }
    }
}
