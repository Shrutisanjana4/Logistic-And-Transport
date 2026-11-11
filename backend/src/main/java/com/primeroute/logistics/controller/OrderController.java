package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.OrderRequest;
import com.primeroute.logistics.dto.OrderResponse;
import com.primeroute.logistics.dto.QuoteRequest;
import com.primeroute.logistics.dto.QuoteResponse;
import com.primeroute.logistics.service.OrderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // CALCULATE QUOTE
    // =========================================================

    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<QuoteResponse>> calculateQuote(
            @Valid @RequestBody QuoteRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        QuoteResponse quote = orderService.calculateQuote(email, request);

        ApiResponse<QuoteResponse> response =
                new ApiResponse<>(
                        true,
                        "Quote calculated successfully",
                        quote
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        OrderResponse order =
                orderService.createOrder(email, request);

        ApiResponse<OrderResponse> response =
                new ApiResponse<>(
                        true,
                        "Order created successfully",
                        order
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =========================================================
    // GET ALL ORDERS
    // =========================================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            Authentication authentication) {

        String email = authentication.getName();

        List<OrderResponse> orders =
                orderService.getAllOrders(email);

        ApiResponse<List<OrderResponse>> response =
                new ApiResponse<>(
                        true,
                        "Orders retrieved successfully",
                        orders
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // GET ORDER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        OrderResponse order =
                orderService.getOrderById(email, id);

        ApiResponse<OrderResponse> response =
                new ApiResponse<>(
                        true,
                        "Order retrieved successfully",
                        order
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // UPDATE ORDER
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        OrderResponse order =
                orderService.updateOrder(
                        email,
                        id,
                        request
                );

        ApiResponse<OrderResponse> response =
                new ApiResponse<>(
                        true,
                        "Order updated successfully",
                        order
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // CANCEL ORDER
    // =========================================================

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        OrderResponse order = orderService.cancelOrder(email, id);

        ApiResponse<OrderResponse> response =
                new ApiResponse<>(
                        true,
                        "Order cancelled successfully",
                        order
                );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // DELETE ORDER (delegates to cancelOrder)
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        orderService.deleteOrder(email, id);

        ApiResponse<Void> response =
                new ApiResponse<>(
                        true,
                        "Order cancelled successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }
}
