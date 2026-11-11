package com.primeroute.logistics.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdjustStockRequest {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "New total stock quantity is required")
    @Min(value = 0, message = "Total stock cannot be negative")
    private Integer newTotalStock;

    @NotBlank(message = "Reason for adjustment is mandatory")
    private String reason;

    public AdjustStockRequest() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Integer getNewTotalStock() { return newTotalStock; }
    public void setNewTotalStock(Integer newTotalStock) { this.newTotalStock = newTotalStock; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
