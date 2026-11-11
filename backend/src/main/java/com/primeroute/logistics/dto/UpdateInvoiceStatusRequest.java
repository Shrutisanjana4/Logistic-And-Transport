package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.InvoiceStatus;
import com.primeroute.logistics.entity.PaymentStatus;

public class UpdateInvoiceStatusRequest {

    private InvoiceStatus invoiceStatus;
    private PaymentStatus paymentStatus;
    private Double amountPaid;
    private String paymentMethod;

    public UpdateInvoiceStatusRequest() {
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
