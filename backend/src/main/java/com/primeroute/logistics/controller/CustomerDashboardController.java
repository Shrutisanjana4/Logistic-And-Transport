package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.CustomerDashboardDTO;
import com.primeroute.logistics.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/dashboard")
public class CustomerDashboardController {

    private final OrderService orderService;

    public CustomerDashboardController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CustomerDashboardDTO>> getDashboardStats(
            Authentication authentication) {

        String email = authentication.getName();

        CustomerDashboardDTO stats = orderService.getDashboardStats(email);

        ApiResponse<CustomerDashboardDTO> response =
                new ApiResponse<>(
                        true,
                        "Dashboard stats retrieved successfully",
                        stats
                );

        return ResponseEntity.ok(response);
    }
}