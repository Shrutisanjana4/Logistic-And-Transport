package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.DeliveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class QuoteRequest {

    @NotNull(message = "Pickup address is required")
    @Positive(message = "Pickup address ID must be valid")
    private Long pickupAddressId;

    @NotNull(message = "Delivery address is required")
    @Positive(message = "Delivery address ID must be valid")
    private Long deliveryAddressId;

    @NotNull(message = "Package weight is required")
    @DecimalMin(value = "0.01", message = "Package weight must be greater than 0")
    private Double packageWeight;

    @NotNull(message = "Package quantity is required")
    @Min(value = 1, message = "Package quantity must be at least 1")
    private Integer packageQuantity;

    @NotNull(message = "Delivery type is required")
    private DeliveryType deliveryType;

    public QuoteRequest() {
    }

    public Long getPickupAddressId() {
        return pickupAddressId;
    }

    public void setPickupAddressId(Long pickupAddressId) {
        this.pickupAddressId = pickupAddressId;
    }

    public Long getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(Long deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public Double getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(Double packageWeight) {
        this.packageWeight = packageWeight;
    }

    public Integer getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Integer packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }
}
