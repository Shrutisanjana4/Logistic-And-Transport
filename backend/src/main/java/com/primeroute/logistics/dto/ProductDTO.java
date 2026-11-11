package com.primeroute.logistics.dto;

public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private String description;
    private String category;
    private Double unitWeight;
    private String unitOfMeasure;
    private Double unitPrice;
    private Integer reorderLevel;
    private Boolean active;

    public ProductDTO() {}

    public ProductDTO(Long id, String sku, String name, String description, String category,
                      Double unitWeight, String unitOfMeasure, Double unitPrice,
                      Integer reorderLevel, Boolean active) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.unitWeight = unitWeight;
        this.unitOfMeasure = unitOfMeasure;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getUnitWeight() { return unitWeight; }
    public void setUnitWeight(Double unitWeight) { this.unitWeight = unitWeight; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
