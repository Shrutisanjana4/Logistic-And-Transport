package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.AdminRouteDTO;
import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.CreateRouteRequest;
import com.primeroute.logistics.dto.UpdateRouteRequest;
import com.primeroute.logistics.entity.RouteStatus;
import com.primeroute.logistics.service.AdminRouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRouteController {

    private final AdminRouteService adminRouteService;

    public AdminRouteController(AdminRouteService adminRouteService) {
        this.adminRouteService = adminRouteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminRouteDTO>>> getAllRoutes() {
        List<AdminRouteDTO> routes = adminRouteService.getAllRoutes();
        return ResponseEntity.ok(new ApiResponse<>(true, "Freight routes retrieved successfully", routes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminRouteDTO>> getRouteById(@PathVariable Long id) {
        AdminRouteDTO route = adminRouteService.getRouteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Route retrieved successfully", route));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminRouteDTO>> createRoute(@Valid @RequestBody CreateRouteRequest req) {
        AdminRouteDTO created = adminRouteService.createRoute(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Freight route created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminRouteDTO>> updateRoute(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRouteRequest req) {
        AdminRouteDTO updated = adminRouteService.updateRoute(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Freight route updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminRouteDTO>> updateRouteStatus(
            @PathVariable Long id,
            @RequestParam RouteStatus status) {
        AdminRouteDTO updated = adminRouteService.updateRouteStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Route status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@PathVariable Long id) {
        adminRouteService.deleteRoute(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Freight route deleted successfully", null));
    }

    @GetMapping("/{id}/shipments")
    public ResponseEntity<ApiResponse<List<com.primeroute.logistics.dto.RouteShipmentDTO>>> getRouteShipments(@PathVariable Long id) {
        List<com.primeroute.logistics.dto.RouteShipmentDTO> shipments = adminRouteService.getShipmentsByRoute(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Route shipments retrieved successfully", shipments));
    }
}