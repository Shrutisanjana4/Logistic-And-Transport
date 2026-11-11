package com.primeroute.logistics.controller;

import java.security.Principal;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.driver.*;
import com.primeroute.logistics.dto.driver.DriverLocationUpdateRequest;
import com.primeroute.logistics.service.DriverPortalService;
import com.primeroute.logistics.service.NotificationService;
import com.primeroute.logistics.repository.UserRepository;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.dto.NotificationDTO;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
@PreAuthorize("hasRole('DRIVER')")
public class DriverPortalController {

    @Autowired
    private DriverPortalService driverPortalService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DriverProfileDTO>> getDriverProfile() {
        DriverProfileDTO profile = driverPortalService.getDriverProfile();
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver profile retrieved successfully", profile));
    }

    @GetMapping("/vehicle")
    public ResponseEntity<ApiResponse<DriverVehicleDTO>> getAssignedVehicle() {
        DriverVehicleDTO vehicle = driverPortalService.getAssignedVehicle();
        if (vehicle == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "No vehicle currently assigned", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Assigned vehicle retrieved successfully", vehicle));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DriverDashboardDTO>> getDriverDashboard() {
        DriverDashboardDTO dashboard = driverPortalService.getDriverDashboard();
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver dashboard data retrieved successfully", dashboard));
    }

    @GetMapping("/deliveries")
    public ResponseEntity<ApiResponse<List<DriverDeliveryDTO>>> getDeliveries(
            @RequestParam(value = "status", required = false) String status) {
        List<DriverDeliveryDTO> deliveries = driverPortalService.getDeliveries(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver deliveries retrieved successfully", deliveries));
    }

    @GetMapping("/deliveries/active")
    public ResponseEntity<ApiResponse<DriverDeliveryDTO>> getActiveDelivery() {
        DriverDeliveryDTO active = driverPortalService.getActiveDelivery();
        if (active == null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "No active delivery assigned", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Active delivery retrieved successfully", active));
    }

    @GetMapping("/deliveries/{id}")
    public ResponseEntity<?> getDeliveryById(@PathVariable("id") Long id) {
        try {
            DriverDeliveryDTO delivery = driverPortalService.getDeliveryById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Delivery retrieved successfully", delivery));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * STEP 2: Mark active shipment as OUT_FOR_DELIVERY.
     */
    @PostMapping("/deliveries/{id}/out-for-delivery")
    public ResponseEntity<?> markOutForDelivery(@PathVariable("id") Long id) {
        try {
            DriverDeliveryDTO updated = driverPortalService.markOutForDelivery(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Delivery status updated to OUT_FOR_DELIVERY", updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * STEP 2: Start Trip / Start Delivery.
     */
    @PostMapping("/deliveries/{id}/start-trip")
    public ResponseEntity<?> startTrip(@PathVariable("id") Long id) {
        try {
            DriverDeliveryDTO updated = driverPortalService.startTrip(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Trip started successfully", updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    /**
     * STEP 3: Complete delivery with electronic proof of delivery (ePOD).
     */
    @PostMapping("/deliveries/{id}/complete")
    public ResponseEntity<?> completeDelivery(
            @PathVariable("id") Long id,
            @RequestBody CompleteDeliveryRequest request) {
        try {
            DriverDeliveryDTO updated = driverPortalService.completeDelivery(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Delivery completed successfully with verified ePOD", updated));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to complete delivery: " + e.getMessage(), null));
        }
    }

    /**
     * STEP 3: Alias endpoint for proof-of-delivery submission.
     */
    @PostMapping("/deliveries/{id}/proof-of-delivery")
    public ResponseEntity<?> submitProofOfDelivery(
            @PathVariable("id") Long id,
            @RequestBody CompleteDeliveryRequest request) {
        return completeDelivery(id, request);
    }

    /**
     * General status transition endpoint for driver portal.
     */
    @PostMapping("/deliveries/{id}/status")
    public ResponseEntity<?> updateDeliveryStatus(
            @PathVariable("id") Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        String targetStatus = payload != null ? payload.get("status") : null;
        if ("OUT_FOR_DELIVERY".equalsIgnoreCase(targetStatus)) {
            return markOutForDelivery(id);
        } else if ("IN_TRANSIT".equalsIgnoreCase(targetStatus)) {
            return startTrip(id);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Unsupported generic status transition. Delivery completion requires ePOD via /complete endpoint.", null));
    }

    // ==========================================
    // REAL-TIME GPS TELEMETRY (DRIVER-ONLY)
    // ==========================================

    @PostMapping("/location")
    public ResponseEntity<ApiResponse<DriverDeliveryDTO>> updateDriverLocation(
            @RequestBody DriverLocationUpdateRequest request,
            Principal principal) {
        DriverDeliveryDTO dto = driverPortalService.updateDriverLocation(principal, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Live GPS telemetry recorded successfully", dto));
    }

    @PostMapping("/deliveries/{id}/location")
    public ResponseEntity<ApiResponse<DriverDeliveryDTO>> updateDeliveryLocation(
            @PathVariable Long id,
            @RequestBody(required = false) DriverLocationUpdateRequest request,
            Principal principal) {
        if (request == null) {
            request = new DriverLocationUpdateRequest();
        }
        request.setShipmentId(id);
        DriverDeliveryDTO dto = driverPortalService.updateDriverLocation(principal, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Live GPS telemetry recorded successfully", dto));
    }

    /**
     * Retrieves dynamic road navigation route, coordinates, distance, and ETA for a specific shipment.
     */
    @GetMapping("/deliveries/{id}/route")
    public ResponseEntity<?> getDeliveryRoute(@PathVariable("id") Long id) {
        try {
            DriverRouteDTO route = driverPortalService.getDeliveryRoute(id);
            if (route == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "No delivery route found for shipment #" + id, null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Delivery route retrieved successfully", route));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to calculate route: " + e.getMessage(), null));
        }
    }

    /**
     * Retrieves dynamic road navigation route for the driver's active delivery.
     */
    @GetMapping("/deliveries/active/route")
    public ResponseEntity<?> getActiveDeliveryRoute() {
        try {
            DriverRouteDTO route = driverPortalService.getActiveDeliveryRoute();
            if (route == null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "No active delivery route assigned", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Active delivery route retrieved successfully", route));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to calculate active delivery route: " + e.getMessage(), null));
        }
    }


    // ==========================================
    // DRIVER NOTIFICATIONS (ROLE_DRIVER)
    // ==========================================

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getDriverNotifications(Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<NotificationDTO> list = notificationService.getUserNotifications(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver notifications retrieved successfully", list));
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDriverUnreadCount(Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        long count = notificationService.getUnreadCount(user);
        Map<String, Long> res = new HashMap<>();
        res.put("unreadCount", count);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver unread count retrieved successfully", res));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markDriverNotificationRead(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationService.markAsRead(id, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver notification marked as read", null));
    }

    @PatchMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllDriverNotificationsRead(Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "All driver notifications marked as read", null));
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDriverNotification(
            @PathVariable Long id,
            Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .or(() -> userRepository.findByEmail(authentication.getName()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        notificationService.deleteNotification(id, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Driver notification deleted successfully", null));
    }

}
