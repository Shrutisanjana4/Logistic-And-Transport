package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.AdminDriverDTO;
import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.CreateDriverRequest;
import com.primeroute.logistics.dto.UpdateDriverRequest;
import com.primeroute.logistics.service.AdminDriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/drivers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDriverController {

    private final AdminDriverService adminDriverService;

    public AdminDriverController(AdminDriverService adminDriverService) {
        this.adminDriverService = adminDriverService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminDriverDTO>>> getAllDrivers() {
        List<AdminDriverDTO> drivers = adminDriverService.getAllDrivers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Drivers retrieved successfully", drivers));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AdminDriverDTO>>> getAvailableDrivers() {
        List<AdminDriverDTO> available = adminDriverService.getAvailableDrivers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Available drivers retrieved successfully", available));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminDriverDTO>> getDriverById(@PathVariable Long id) {
        AdminDriverDTO driver = adminDriverService.getDriverById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver details retrieved", driver));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminDriverDTO>> createDriver(@Valid @RequestBody CreateDriverRequest request) {
        AdminDriverDTO created = adminDriverService.createDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Driver registered successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminDriverDTO>> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDriverRequest request) {
        AdminDriverDTO updated = adminDriverService.updateDriver(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long id) {
        adminDriverService.deleteDriver(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver deleted or deactivated successfully", null));
    }
}