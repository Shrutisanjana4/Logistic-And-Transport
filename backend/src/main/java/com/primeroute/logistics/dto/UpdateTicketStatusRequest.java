package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTicketStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    public UpdateTicketStatusRequest() {
    }

    public UpdateTicketStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}