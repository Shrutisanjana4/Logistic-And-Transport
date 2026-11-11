package com.primeroute.logistics.dto.report;

import java.util.List;

public class CustomerAnalyticsReportDTO {
    private long totalCustomers;
    private long activeCustomers;
    private long newCustomersThisPeriod;
    private long customersWithOrders;
    private double ordersPerCustomerAvg;
    private double customerRetentionRate;
    private List<CustomerStatItemDTO> topCustomersByVolume;
    private List<CustomerStatItemDTO> topCustomersByRevenue;

    public CustomerAnalyticsReportDTO() {}

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(long activeCustomers) { this.activeCustomers = activeCustomers; }
    public long getNewCustomersThisPeriod() { return newCustomersThisPeriod; }
    public void setNewCustomersThisPeriod(long newCustomersThisPeriod) { this.newCustomersThisPeriod = newCustomersThisPeriod; }
    public long getCustomersWithOrders() { return customersWithOrders; }
    public void setCustomersWithOrders(long customersWithOrders) { this.customersWithOrders = customersWithOrders; }
    public double getOrdersPerCustomerAvg() { return ordersPerCustomerAvg; }
    public void setOrdersPerCustomerAvg(double ordersPerCustomerAvg) { this.ordersPerCustomerAvg = ordersPerCustomerAvg; }
    public double getCustomerRetentionRate() { return customerRetentionRate; }
    public void setCustomerRetentionRate(double customerRetentionRate) { this.customerRetentionRate = customerRetentionRate; }
    public List<CustomerStatItemDTO> getTopCustomersByVolume() { return topCustomersByVolume; }
    public void setTopCustomersByVolume(List<CustomerStatItemDTO> topCustomersByVolume) { this.topCustomersByVolume = topCustomersByVolume; }
    public List<CustomerStatItemDTO> getTopCustomersByRevenue() { return topCustomersByRevenue; }
    public void setTopCustomersByRevenue(List<CustomerStatItemDTO> topCustomersByRevenue) { this.topCustomersByRevenue = topCustomersByRevenue; }

    public static class CustomerStatItemDTO {
        private Long id;
        private String name;
        private String company;
        private String email;
        private long orderCount;
        private double totalSpent;
        private double totalInvoiced;
        private double totalPaid;

        public CustomerStatItemDTO() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public long getOrderCount() { return orderCount; }
        public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        public double getTotalInvoiced() { return totalInvoiced; }
        public void setTotalInvoiced(double totalInvoiced) { this.totalInvoiced = totalInvoiced; }
        public double getTotalPaid() { return totalPaid; }
        public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
    }
}
