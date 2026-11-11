package com.primeroute.logistics.dto.report;

public class DeliveryPerformanceDTO {
    private double deliverySuccessRate;
    private double onTimeDeliveryRate;
    private double averageTransitHours;
    private long delayedShipments;
    private long deliveredToday;
    private long deliveredThisWeek;
    private long deliveredThisMonth;

    public DeliveryPerformanceDTO() {}

    public double getDeliverySuccessRate() { return deliverySuccessRate; }
    public void setDeliverySuccessRate(double deliverySuccessRate) { this.deliverySuccessRate = deliverySuccessRate; }
    public double getOnTimeDeliveryRate() { return onTimeDeliveryRate; }
    public void setOnTimeDeliveryRate(double onTimeDeliveryRate) { this.onTimeDeliveryRate = onTimeDeliveryRate; }
    public double getAverageTransitHours() { return averageTransitHours; }
    public void setAverageTransitHours(double averageTransitHours) { this.averageTransitHours = averageTransitHours; }
    public long getDelayedShipments() { return delayedShipments; }
    public void setDelayedShipments(long delayedShipments) { this.delayedShipments = delayedShipments; }
    public long getDeliveredToday() { return deliveredToday; }
    public void setDeliveredToday(long deliveredToday) { this.deliveredToday = deliveredToday; }
    public long getDeliveredThisWeek() { return deliveredThisWeek; }
    public void setDeliveredThisWeek(long deliveredThisWeek) { this.deliveredThisWeek = deliveredThisWeek; }
    public long getDeliveredThisMonth() { return deliveredThisMonth; }
    public void setDeliveredThisMonth(long deliveredThisMonth) { this.deliveredThisMonth = deliveredThisMonth; }
}
