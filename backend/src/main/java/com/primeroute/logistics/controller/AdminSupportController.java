package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.CreateTicketMessageRequest;
import com.primeroute.logistics.dto.SupportTicketDTO;
import com.primeroute.logistics.dto.SupportTicketMessageDTO;
import com.primeroute.logistics.dto.UpdateTicketPriorityRequest;
import com.primeroute.logistics.dto.UpdateTicketStatusRequest;
import com.primeroute.logistics.service.SupportTicketService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/support")
public class AdminSupportController {

    private final SupportTicketService supportTicketService;

    public AdminSupportController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicketDTO>>> getAllTickets() {
        List<SupportTicketDTO> tickets = supportTicketService.getAllTicketsForAdmin();
        ApiResponse<List<SupportTicketDTO>> response =
                new ApiResponse<>(true, "All support tickets retrieved successfully", tickets);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> getTicketById(@PathVariable Long id) {
        SupportTicketDTO ticket = supportTicketService.getTicketByIdForAdmin(id);
        ApiResponse<SupportTicketDTO> response =
                new ApiResponse<>(true, "Support ticket details retrieved successfully", ticket);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tickets/{id}/status")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {

        SupportTicketDTO updated = supportTicketService.updateTicketStatus(id, request.getStatus());
        ApiResponse<SupportTicketDTO> response =
                new ApiResponse<>(true, "Ticket status updated successfully", updated);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tickets/{id}/priority")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> updatePriority(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketPriorityRequest request) {

        SupportTicketDTO updated = supportTicketService.updateTicketPriority(id, request.getPriority());
        ApiResponse<SupportTicketDTO> response =
                new ApiResponse<>(true, "Ticket priority updated successfully", updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/{id}/messages")
    public ResponseEntity<ApiResponse<List<SupportTicketMessageDTO>>> getMessages(@PathVariable Long id) {
        List<SupportTicketMessageDTO> messages = supportTicketService.getAdminMessages(id);
        ApiResponse<List<SupportTicketMessageDTO>> response =
                new ApiResponse<>(true, "Ticket conversation messages retrieved successfully", messages);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{id}/messages")
    public ResponseEntity<ApiResponse<SupportTicketMessageDTO>> addMessage(
            @PathVariable Long id,
            @Valid @RequestBody CreateTicketMessageRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        SupportTicketMessageDTO message = supportTicketService.addAdminMessage(email, id, request.getMessage());
        ApiResponse<SupportTicketMessageDTO> response =
                new ApiResponse<>(true, "Support agent reply recorded successfully", message);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}