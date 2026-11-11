package com.primeroute.logistics.dto.warehouse;

import java.time.LocalDateTime;

public class WarehouseTransactionDTO {

    private Long id;
    private String sku;
    private String productName;
    private String transactionType;
    private Integer quantity;
    private Integer previousAvailable;
    private Integer newAvailable;
    private String performedBy;
    private LocalDateTime timestamp;
    private String reason;
    private String referenceOrder;
    private String referenceShipment;

    public WarehouseTransactionDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPreviousAvailable() { return previousAvailable; }
    public void setPreviousAvailable(Integer previousAvailable) { this.previousAvailable = previousAvailable; }

    public Integer getNewAvailable() { return newAvailable; }
    public void setNewAvailable(Integer newAvailable) { this.newAvailable = newAvailable; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getReferenceOrder() { return referenceOrder; }
    public void setReferenceOrder(String referenceOrder) { this.referenceOrder = referenceOrder; }

    public String getReferenceShipment() { return referenceShipment; }
    public void setReferenceShipment(String referenceShipment) { this.referenceShipment = referenceShipment; }
}
