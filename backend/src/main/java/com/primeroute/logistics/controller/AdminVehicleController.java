package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.service.AdminVehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/vehicles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVehicleController {

    private final AdminVehicleService adminVehicleService;

    public AdminVehicleController(AdminVehicleService adminVehicleService) {
        this.adminVehicleService = adminVehicleService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getAllVehicles() {
        List<VehicleDTO> list = adminVehicleService.getAllVehicles();
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicles retrieved successfully", list));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<VehicleDTO>>> getAvailableVehicles() {
        List<VehicleDTO> available = adminVehicleService.getAvailableVehicles();
        return ResponseEntity.ok(new ApiResponse<>(true, "Available vehicles retrieved successfully", available));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDTO>> getVehicleById(@PathVariable Long id) {
        VehicleDTO dto = adminVehicleService.getVehicleById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle retrieved successfully", dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleDTO>> createVehicle(@Valid @RequestBody CreateVehicleRequest req) {
        VehicleDTO created = adminVehicleService.createVehicle(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Vehicle registered successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest req) {
        VehicleDTO updated = adminVehicleService.updateVehicle(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle updated successfully", updated));
    }

    @PatchMapping("/{id}/driver")
    public ResponseEntity<ApiResponse<VehicleDTO>> assignDriver(
            @PathVariable Long id,
            @RequestBody AssignDriverRequest req) {
        VehicleDTO updated = adminVehicleService.assignDriver(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver assignment updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleStatusRequest req) {
        VehicleDTO updated = adminVehicleService.updateStatus(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateVehicle(@PathVariable Long id) {
        adminVehicleService.deactivateVehicle(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Vehicle deactivated successfully", null));
    }

    @GetMapping("/{id}/shipment")
    public ResponseEntity<ApiResponse<VehicleDTO>> getVehicleActiveShipment(@PathVariable Long id) {
        VehicleDTO dto = adminVehicleService.getVehicleById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Active shipment retrieved successfully", dto));
    }
}