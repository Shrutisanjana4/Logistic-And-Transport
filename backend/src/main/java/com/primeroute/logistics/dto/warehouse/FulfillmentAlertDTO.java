package com.primeroute.logistics.dto.warehouse;

public class FulfillmentAlertDTO {
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private Long requiredQuantity;
    private Integer availableStock;
    private Long pendingOrdersCount;
    private String status; // "AVAILABLE" or "INSUFFICIENT_STOCK"

    public FulfillmentAlertDTO() {}

    public FulfillmentAlertDTO(Long productId, String sku, String productName, String category,
                               Long requiredQuantity, Integer availableStock, Long pendingOrdersCount, String status) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.category = category;
        this.requiredQuantity = requiredQuantity;
        this.availableStock = availableStock;
        this.pendingOrdersCount = pendingOrdersCount;
        this.status = status;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(Long requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public Long getPendingOrdersCount() { return pendingOrdersCount; }
    public void setPendingOrdersCount(Long pendingOrdersCount) { this.pendingOrdersCount = pendingOrdersCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
