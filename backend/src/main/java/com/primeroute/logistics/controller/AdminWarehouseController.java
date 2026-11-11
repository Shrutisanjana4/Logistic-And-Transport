package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.service.AdminWarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWarehouseController {

    private final AdminWarehouseService adminWarehouseService;

    public AdminWarehouseController(AdminWarehouseService adminWarehouseService) {
        this.adminWarehouseService = adminWarehouseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehouses() {
        List<WarehouseDTO> list = adminWarehouseService.getAllWarehouses();
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouses retrieved successfully", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseById(@PathVariable Long id) {
        WarehouseDTO dto = adminWarehouseService.getWarehouseById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouse retrieved successfully", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@Valid @RequestBody CreateWarehouseRequest req) {
        WarehouseDTO created = adminWarehouseService.createWarehouse(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Warehouse facility registered successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWarehouseRequest req) {
        WarehouseDTO updated = adminWarehouseService.updateWarehouse(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouse facility updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWarehouseStatusRequest req) {
        WarehouseDTO updated = adminWarehouseService.updateStatus(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouse status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateWarehouse(@PathVariable Long id) {
        adminWarehouseService.deactivateWarehouse(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouse deactivated successfully", null));
    }
}
