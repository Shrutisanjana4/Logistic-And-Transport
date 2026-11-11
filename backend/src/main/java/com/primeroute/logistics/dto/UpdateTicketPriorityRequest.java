package com.primeroute.logistics.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateTicketPriorityRequest {

    @NotBlank(message = "Priority is required")
    private String priority;

    public UpdateTicketPriorityRequest() {
    }

    public UpdateTicketPriorityRequest(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}