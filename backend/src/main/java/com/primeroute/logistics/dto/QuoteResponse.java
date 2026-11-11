package com.primeroute.logistics.dto;

public class QuoteResponse {

    private Double baseFee;
    private Double weightCharge;
    private Double prioritySurcharge;
    private Double subtotal;
    private Double gst;
    private Double totalAmount;

    public QuoteResponse() {
    }

    public QuoteResponse(Double baseFee, Double weightCharge, Double prioritySurcharge, Double subtotal, Double gst, Double totalAmount) {
        this.baseFee = baseFee;
        this.weightCharge = weightCharge;
        this.prioritySurcharge = prioritySurcharge;
        this.subtotal = subtotal;
        this.gst = gst;
        this.totalAmount = totalAmount;
    }

    public Double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(Double baseFee) {
        this.baseFee = baseFee;
    }

    public Double getWeightCharge() {
        return weightCharge;
    }

    public void setWeightCharge(Double weightCharge) {
        this.weightCharge = weightCharge;
    }

    public Double getPrioritySurcharge() {
        return prioritySurcharge;
    }

    public void setPrioritySurcharge(Double prioritySurcharge) {
        this.prioritySurcharge = prioritySurcharge;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
