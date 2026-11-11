package com.primeroute.logistics.dto;

public class InvoiceSummaryDTO {

    private long totalInvoices;
    private double totalBilled;
    private double paidAmount;
    private double outstandingAmount;
    private double overdueAmount;

    public InvoiceSummaryDTO() {
    }

    public InvoiceSummaryDTO(long totalInvoices, double totalBilled, double paidAmount, double outstandingAmount, double overdueAmount) {
        this.totalInvoices = totalInvoices;
        this.totalBilled = totalBilled;
        this.paidAmount = paidAmount;
        this.outstandingAmount = outstandingAmount;
        this.overdueAmount = overdueAmount;
    }

    public long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public double getTotalBilled() {
        return totalBilled;
    }

    public void setTotalBilled(double totalBilled) {
        this.totalBilled = totalBilled;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public double getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(double overdueAmount) {
        this.overdueAmount = overdueAmount;
    }
}
