package com.primeroute.logistics.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class AdminNotificationSettingsDTO {
    @JsonAlias({"masterEmailNotifications"})
    private boolean emailNotifications = true;

    @JsonAlias({"orderStatusUpdates"})
    private boolean orderUpdates = true;

    @JsonAlias({"shipmentTransitCheckpoints"})
    private boolean shipmentUpdates = true;

    @JsonAlias({"deliveryPodNotifications"})
    private boolean deliveryUpdates = true;

    @JsonAlias({"invoiceBillingAlerts"})
    private boolean invoiceNotifications = true;

    private boolean supportTicketNotifications = true;

    public AdminNotificationSettingsDTO() {}

    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public boolean isMasterEmailNotifications() { return emailNotifications; }
    public void setMasterEmailNotifications(boolean masterEmailNotifications) { this.emailNotifications = masterEmailNotifications; }

    public boolean isOrderUpdates() { return orderUpdates; }
    public void setOrderUpdates(boolean orderUpdates) { this.orderUpdates = orderUpdates; }

    public boolean isOrderStatusUpdates() { return orderUpdates; }
    public void setOrderStatusUpdates(boolean orderStatusUpdates) { this.orderUpdates = orderStatusUpdates; }

    public boolean isShipmentUpdates() { return shipmentUpdates; }
    public void setShipmentUpdates(boolean shipmentUpdates) { this.shipmentUpdates = shipmentUpdates; }

    public boolean isShipmentTransitCheckpoints() { return shipmentUpdates; }
    public void setShipmentTransitCheckpoints(boolean shipmentTransitCheckpoints) { this.shipmentUpdates = shipmentTransitCheckpoints; }

    public boolean isDeliveryUpdates() { return deliveryUpdates; }
    public void setDeliveryUpdates(boolean deliveryUpdates) { this.deliveryUpdates = deliveryUpdates; }

    public boolean isDeliveryPodNotifications() { return deliveryUpdates; }
    public void setDeliveryPodNotifications(boolean deliveryPodNotifications) { this.deliveryUpdates = deliveryPodNotifications; }

    public boolean isInvoiceNotifications() { return invoiceNotifications; }
    public void setInvoiceNotifications(boolean invoiceNotifications) { this.invoiceNotifications = invoiceNotifications; }

    public boolean isInvoiceBillingAlerts() { return invoiceNotifications; }
    public void setInvoiceBillingAlerts(boolean invoiceBillingAlerts) { this.invoiceNotifications = invoiceBillingAlerts; }

    public boolean isSupportTicketNotifications() { return supportTicketNotifications; }
    public void setSupportTicketNotifications(boolean supportTicketNotifications) { this.supportTicketNotifications = supportTicketNotifications; }
}
