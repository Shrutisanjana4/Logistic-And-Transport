package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.report.*;
import com.primeroute.logistics.service.AdminReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final AdminReportService reportService;

    public AdminReportController(AdminReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ComprehensiveReportDTO>> getDashboardReport(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        ComprehensiveReportDTO report = reportService.getComprehensiveReport(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comprehensive logistics reports retrieved successfully", report));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<ReportOverviewDTO>> getOverview(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        ReportOverviewDTO data = reportService.getOperationsOverview(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Operations overview retrieved successfully", data));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<OrderAnalyticsDTO>> getOrderAnalytics(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        OrderAnalyticsDTO data = reportService.getOrderAnalytics(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order analytics retrieved successfully", data));
    }

    @GetMapping("/shipments")
    public ResponseEntity<ApiResponse<ShipmentAnalyticsDTO>> getShipmentAnalytics(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        ShipmentAnalyticsDTO data = reportService.getShipmentAnalytics(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipment analytics retrieved successfully", data));
    }

    @GetMapping("/delivery-performance")
    public ResponseEntity<ApiResponse<DeliveryPerformanceDTO>> getDeliveryPerformance(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        DeliveryPerformanceDTO data = reportService.getDeliveryPerformance(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Delivery performance retrieved successfully", data));
    }

    @GetMapping("/fleet")
    public ResponseEntity<ApiResponse<FleetAnalyticsDTO>> getFleetAnalytics() {
        FleetAnalyticsDTO data = reportService.getFleetAnalytics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Fleet analytics retrieved successfully", data));
    }

    @GetMapping("/drivers")
    public ResponseEntity<ApiResponse<DriverPerformanceReportDTO>> getDriverPerformance() {
        DriverPerformanceReportDTO data = reportService.getDriverPerformance();
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver performance report retrieved successfully", data));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<RoutePerformanceReportDTO>> getRoutePerformance() {
        RoutePerformanceReportDTO data = reportService.getRoutePerformance();
        return ResponseEntity.ok(new ApiResponse<>(true, "Route performance report retrieved successfully", data));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<ApiResponse<WarehouseAnalyticsReportDTO>> getWarehouseAnalytics() {
        WarehouseAnalyticsReportDTO data = reportService.getWarehouseAnalytics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Warehouse analytics retrieved successfully", data));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<CustomerAnalyticsReportDTO>> getCustomerAnalytics(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        CustomerAnalyticsReportDTO data = reportService.getCustomerAnalytics(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer analytics retrieved successfully", data));
    }

    @GetMapping("/financial")
    public ResponseEntity<ApiResponse<FinancialAnalyticsReportDTO>> getFinancialAnalytics(
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String range,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        FinancialAnalyticsReportDTO data = reportService.getFinancialAnalytics(range, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "Financial analytics retrieved successfully", data));
    }
}
