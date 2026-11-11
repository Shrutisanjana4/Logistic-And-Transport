package com.primeroute.logistics.dto.report;

import java.util.List;
import java.util.Map;

public class FinancialAnalyticsReportDTO {
    private long totalInvoices;
    private double totalBilled;
    private double totalPaid;
    private double outstandingAmount;
    private double overdueAmount;
    private double averageInvoiceValue;
    private long paidCount;
    private long unpaidCount;
    private long partiallyPaidCount;
    private long overdueCount;
    private Map<String, Long> paymentMethodBreakdown;
    private List<MonthlyFinancialBreakdownDTO> monthlyBreakdown;

    public FinancialAnalyticsReportDTO() {}

    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }
    public double getTotalBilled() { return totalBilled; }
    public void setTotalBilled(double totalBilled) { this.totalBilled = totalBilled; }
    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
    public double getOutstandingAmount() { return outstandingAmount; }
    public void setOutstandingAmount(double outstandingAmount) { this.outstandingAmount = outstandingAmount; }
    public double getOverdueAmount() { return overdueAmount; }
    public void setOverdueAmount(double overdueAmount) { this.overdueAmount = overdueAmount; }
    public double getAverageInvoiceValue() { return averageInvoiceValue; }
    public void setAverageInvoiceValue(double averageInvoiceValue) { this.averageInvoiceValue = averageInvoiceValue; }
    public long getPaidCount() { return paidCount; }
    public void setPaidCount(long paidCount) { this.paidCount = paidCount; }
    public long getUnpaidCount() { return unpaidCount; }
    public void setUnpaidCount(long unpaidCount) { this.unpaidCount = unpaidCount; }
    public long getPartiallyPaidCount() { return partiallyPaidCount; }
    public void setPartiallyPaidCount(long partiallyPaidCount) { this.partiallyPaidCount = partiallyPaidCount; }
    public long getOverdueCount() { return overdueCount; }
    public void setOverdueCount(long overdueCount) { this.overdueCount = overdueCount; }
    public Map<String, Long> getPaymentMethodBreakdown() { return paymentMethodBreakdown; }
    public void setPaymentMethodBreakdown(Map<String, Long> paymentMethodBreakdown) { this.paymentMethodBreakdown = paymentMethodBreakdown; }
    public List<MonthlyFinancialBreakdownDTO> getMonthlyBreakdown() { return monthlyBreakdown; }
    public void setMonthlyBreakdown(List<MonthlyFinancialBreakdownDTO> monthlyBreakdown) { this.monthlyBreakdown = monthlyBreakdown; }

    public static class MonthlyFinancialBreakdownDTO {
        private String month;
        private long orders;
        private double revenue;
        private double avgPerOrder;

        public MonthlyFinancialBreakdownDTO() {}
        public MonthlyFinancialBreakdownDTO(String month, long orders, double revenue, double avgPerOrder) {
            this.month = month;
            this.orders = orders;
            this.revenue = revenue;
            this.avgPerOrder = avgPerOrder;
        }
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public long getOrders() { return orders; }
        public void setOrders(long orders) { this.orders = orders; }
        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
        public double getAvgPerOrder() { return avgPerOrder; }
        public void setAvgPerOrder(double avgPerOrder) { this.avgPerOrder = avgPerOrder; }
    }
}
