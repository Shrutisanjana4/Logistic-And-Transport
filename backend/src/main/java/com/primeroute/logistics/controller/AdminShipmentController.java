package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.service.AdminShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shipments")
public class AdminShipmentController {

    private final AdminShipmentService adminShipmentService;

    public AdminShipmentController(AdminShipmentService adminShipmentService) {
        this.adminShipmentService = adminShipmentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminShipmentDTO>>> getAllShipments() {
        List<AdminShipmentDTO> shipments = adminShipmentService.getAllShipments();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipments retrieved successfully", shipments)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> getShipmentById(@PathVariable Long id) {
        AdminShipmentDTO shipment = adminShipmentService.getShipmentById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipment retrieved successfully", shipment)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> createShipment(
            @Valid @RequestBody AdminCreateShipmentRequest request) {
        AdminShipmentDTO created = adminShipmentService.createShipment(
                request.getOrderId(),
                request.getWarehouseId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Shipment created successfully", created)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> updateShipment(
            @PathVariable Long id,
            @RequestBody AdminUpdateShipmentRequest request) {
        AdminShipmentDTO updated = adminShipmentService.updateShipment(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipment updated successfully", updated)
        );
    }

    @PatchMapping("/{id}/vehicle")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> assignVehicle(
            @PathVariable Long id,
            @RequestBody AssignVehicleRequest request) {
        AdminShipmentDTO updated = adminShipmentService.assignVehicle(id, request.getVehicleId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Vehicle assignment updated successfully", updated)
        );
    }

    @PatchMapping("/{id}/driver")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> assignDriver(
            @PathVariable Long id,
            @RequestBody AssignDriverRequest request) {
        AdminShipmentDTO updated = adminShipmentService.assignDriver(id, request.getDriverId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Driver assignment updated successfully", updated)
        );
    }

    @PutMapping("/{id}/warehouse")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> assignWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody AssignWarehouseRequest request) {
        AdminShipmentDTO updated = adminShipmentService.assignWarehouse(id, request.getWarehouseId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Warehouse assignment updated successfully", updated)
        );
    }

    @PatchMapping("/{id}/warehouse")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> patchWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody AssignWarehouseRequest request) {
        AdminShipmentDTO updated = adminShipmentService.assignWarehouse(id, request.getWarehouseId());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Warehouse assignment updated successfully", updated)
        );
    }

    @PostMapping("/{id}/override-assignment")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> overrideAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AdminOverrideAssignmentRequest request) {
        AdminShipmentDTO updated = adminShipmentService.overrideAssignment(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Fleet assignment overridden successfully", updated)
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminShipmentDTO>> updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateShipmentStatusRequest request) {
        AdminShipmentDTO updated = adminShipmentService.updateShipmentStatus(
                id,
                request.getStatus(),
                request.getLocationName(),
                request.getDescription()
        );
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipment status updated successfully", updated)
        );
    }

    @GetMapping("/unassigned-orders")
    public ResponseEntity<ApiResponse<List<AdminOrderResponseDTO>>> getUnassignedOrders() {
        List<AdminOrderResponseDTO> orders = adminShipmentService.getUnassignedOrders();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Unassigned orders retrieved successfully", orders)
        );
    }
}