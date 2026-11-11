package com.primeroute.logistics.dto;

public class ProductDemandDTO {
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private Long totalOrders;
    private Long requestedQuantity;
    private Double totalWeightKg;

    public ProductDemandDTO() {}

    public ProductDemandDTO(Long productId, String sku, String productName, String category, Long totalOrders, Long requestedQuantity, Double totalWeightKg) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.category = category;
        this.totalOrders = totalOrders;
        this.requestedQuantity = requestedQuantity;
        this.totalWeightKg = totalWeightKg != null ? Math.round(totalWeightKg * 100.0) / 100.0 : 0.0;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }

    public Long getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(Long requestedQuantity) { this.requestedQuantity = requestedQuantity; }

    public Double getTotalWeightKg() { return totalWeightKg; }
    public void setTotalWeightKg(Double totalWeightKg) { this.totalWeightKg = totalWeightKg; }
}
