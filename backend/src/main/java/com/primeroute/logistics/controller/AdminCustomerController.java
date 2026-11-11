package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.service.AdminCustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCustomerController {

    private final AdminCustomerService adminCustomerService;

    public AdminCustomerController(AdminCustomerService adminCustomerService) {
        this.adminCustomerService = adminCustomerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminCustomerDTO>>> getAllCustomers() {
        List<AdminCustomerDTO> customers = adminCustomerService.getAllCustomers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Customers retrieved successfully", customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCustomerDetailDTO>> getCustomerById(@PathVariable Long id) {
        AdminCustomerDetailDTO customer = adminCustomerService.getCustomerById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer details retrieved successfully", customer));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminCustomerDTO>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        AdminCustomerDTO created = adminCustomerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Customer registered successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminCustomerDTO>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {
        AdminCustomerDTO updated = adminCustomerService.updateCustomer(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateCustomer(@PathVariable Long id) {
        adminCustomerService.deactivateCustomer(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer deactivated successfully", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminCustomerDTO>> toggleStatus(@PathVariable Long id) {
        AdminCustomerDTO updated = adminCustomerService.toggleCustomerStatus(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer status updated successfully", updated));
    }
}
