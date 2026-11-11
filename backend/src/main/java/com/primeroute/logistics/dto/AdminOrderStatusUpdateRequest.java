package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class AdminOrderStatusUpdateRequest {

    @NotNull(message = "Status cannot be null")
    private OrderStatus status;

    public AdminOrderStatusUpdateRequest() {
    }

    public AdminOrderStatusUpdateRequest(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
