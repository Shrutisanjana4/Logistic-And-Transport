package com.primeroute.logistics.dto;

public class OrderItemDTO {
    private Long id;
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private Integer quantity;
    private Double unitWeight;
    private Double unitPrice;
    private Double totalWeight;

    public OrderItemDTO() {}

    public OrderItemDTO(Long id, Long productId, String sku, String productName, String category, Integer quantity, Double unitWeight, Double unitPrice, Double totalWeight) {
        this.id = id;
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.unitWeight = unitWeight;
        this.unitPrice = unitPrice;
        this.totalWeight = totalWeight;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitWeight() { return unitWeight; }
    public void setUnitWeight(Double unitWeight) { this.unitWeight = unitWeight; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) { this.totalWeight = totalWeight; }
}
