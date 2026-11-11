package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTicketMessageRequest {

    @NotBlank(message = "Message text is required")
    private String message;

    public CreateTicketMessageRequest() {
    }

    public CreateTicketMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}