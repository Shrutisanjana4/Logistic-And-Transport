package com.primeroute.logistics.dto;

public class MonthlyRevenueStatDTO {
    private String month;
    private double revenue;
    private long orders;
    private long delivered;

    public MonthlyRevenueStatDTO() {}
    public MonthlyRevenueStatDTO(String month, double revenue, long orders, long delivered) {
        this.month = month;
        this.revenue = revenue;
        this.orders = orders;
        this.delivered = delivered;
    }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }
    public long getOrders() { return orders; }
    public void setOrders(long orders) { this.orders = orders; }
    public long getDelivered() { return delivered; }
    public void setDelivered(long delivered) { this.delivered = delivered; }
}
