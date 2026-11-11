package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.AdminDashboardSummaryDTO;
import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.ShipmentTrendPointDTO;
import com.primeroute.logistics.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<AdminDashboardSummaryDTO>> getSummary() {
        AdminDashboardSummaryDTO summary = adminDashboardService.getSummary();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Admin dashboard summary retrieved successfully", summary)
        );
    }

    @GetMapping("/shipment-trends")
    public ResponseEntity<ApiResponse<List<ShipmentTrendPointDTO>>> getShipmentTrends(
            @RequestParam(defaultValue = "7") int days) {
        List<ShipmentTrendPointDTO> trends = adminDashboardService.getShipmentTrends(days);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Shipment trends retrieved successfully", trends)
        );
    }
}
