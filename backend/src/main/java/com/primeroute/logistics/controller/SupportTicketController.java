package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.CreateTicketMessageRequest;
import com.primeroute.logistics.dto.CreateTicketRequest;
import com.primeroute.logistics.dto.SupportContactDTO;
import com.primeroute.logistics.dto.SupportTicketDTO;
import com.primeroute.logistics.dto.SupportTicketMessageDTO;
import com.primeroute.logistics.service.SupportTicketService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/support")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @GetMapping("/contact")
    public ResponseEntity<ApiResponse<SupportContactDTO>> getSupportContact() {
        SupportContactDTO contact = supportTicketService.getSupportContact();
        ApiResponse<SupportContactDTO> response =
                new ApiResponse<>(true, "Support contact details retrieved successfully", contact);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        SupportTicketDTO ticket = supportTicketService.createTicket(email, request);

        ApiResponse<SupportTicketDTO> response =
                new ApiResponse<>(true, "Support ticket submitted successfully", ticket);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicketDTO>>> getCustomerTickets(
            Authentication authentication) {

        String email = authentication.getName();
        List<SupportTicketDTO> tickets = supportTicketService.getCustomerTickets(email);

        ApiResponse<List<SupportTicketDTO>> response =
                new ApiResponse<>(true, "Support tickets retrieved successfully", tickets);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<ApiResponse<SupportTicketDTO>> getTicketById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        SupportTicketDTO ticket = supportTicketService.getTicketById(email, id);

        ApiResponse<SupportTicketDTO> response =
                new ApiResponse<>(true, "Support ticket retrieved successfully", ticket);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tickets/{id}/messages")
    public ResponseEntity<ApiResponse<List<SupportTicketMessageDTO>>> getTicketMessages(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        List<SupportTicketMessageDTO> messages = supportTicketService.getCustomerMessages(email, id);

        ApiResponse<List<SupportTicketMessageDTO>> response =
                new ApiResponse<>(true, "Ticket conversation retrieved successfully", messages);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tickets/{id}/messages")
    public ResponseEntity<ApiResponse<SupportTicketMessageDTO>> addTicketMessage(
            @PathVariable Long id,
            @Valid @RequestBody CreateTicketMessageRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        SupportTicketMessageDTO message = supportTicketService.addCustomerMessage(email, id, request.getMessage());

        ApiResponse<SupportTicketMessageDTO> response =
                new ApiResponse<>(true, "Message sent successfully", message);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}