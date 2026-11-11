package com.primeroute.logistics.dto;

import java.time.LocalDateTime;

public class SupportTicketMessageDTO {
    private Long id;
    private Long ticketId;
    private String senderName;
    private String senderRole;
    private String message;
    private LocalDateTime createdAt;

    public SupportTicketMessageDTO() {
    }

    public SupportTicketMessageDTO(Long id, Long ticketId, String senderName, String senderRole, String message, LocalDateTime createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}