package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.AdminOrderResponseDTO;
import com.primeroute.logistics.dto.AdminOrderStatusUpdateRequest;
import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.service.AdminOrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminOrderResponseDTO>>> getAllOrders() {
        List<AdminOrderResponseDTO> orders = adminOrderService.getAllOrders();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Orders retrieved successfully", orders)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminOrderResponseDTO>> getOrderById(@PathVariable Long id) {
        AdminOrderResponseDTO order = adminOrderService.getOrderById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order retrieved successfully", order)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminOrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminOrderStatusUpdateRequest request) {
        AdminOrderResponseDTO updatedOrder = adminOrderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order status updated successfully", updatedOrder)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminOrderResponseDTO>> cancelOrder(@PathVariable Long id) {
        AdminOrderResponseDTO cancelledOrder = adminOrderService.cancelOrder(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Order cancelled successfully", cancelledOrder)
        );
    }
}
