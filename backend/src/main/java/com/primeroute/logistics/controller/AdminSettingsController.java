package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.service.AdminSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {

    private final AdminSettingsService adminSettingsService;

    public AdminSettingsController(AdminSettingsService adminSettingsService) {
        this.adminSettingsService = adminSettingsService;
    }

    // 1. Profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AdminProfileDTO>> getProfile(Authentication authentication) {
        String email = authentication.getName();
        AdminProfileDTO profile = adminSettingsService.getProfile(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AdminProfileDTO>> updateProfile(
            @Valid @RequestBody AdminProfileUpdateRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        AdminProfileDTO updated = adminSettingsService.updateProfile(email, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin profile updated successfully", updated));
    }

    // 2. Security (Change Password)
    @PostMapping("/security/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody AdminSecurityChangePasswordRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        adminSettingsService.changePassword(email, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    // 3. Notification Preferences
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<AdminNotificationSettingsDTO>> getNotificationSettings() {
        AdminNotificationSettingsDTO settings = adminSettingsService.getNotificationSettings();
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification settings retrieved successfully", settings));
    }

    @PutMapping("/notifications")
    public ResponseEntity<ApiResponse<AdminNotificationSettingsDTO>> updateNotificationSettings(
            @RequestBody AdminNotificationSettingsDTO request) {
        AdminNotificationSettingsDTO updated = adminSettingsService.updateNotificationSettings(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification settings updated successfully", updated));
    }

    // 4. Company / Organization Settings
    @GetMapping("/company")
    public ResponseEntity<ApiResponse<AdminCompanySettingsDTO>> getCompanySettings() {
        AdminCompanySettingsDTO settings = adminSettingsService.getCompanySettings();
        return ResponseEntity.ok(new ApiResponse<>(true, "Company settings retrieved successfully", settings));
    }

    @PutMapping("/company")
    public ResponseEntity<ApiResponse<AdminCompanySettingsDTO>> updateCompanySettings(
            @RequestBody AdminCompanySettingsDTO request) {
        AdminCompanySettingsDTO updated = adminSettingsService.updateCompanySettings(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company settings updated successfully", updated));
    }

    // 5. Support & Contact Settings
    @GetMapping("/support")
    public ResponseEntity<ApiResponse<AdminSupportSettingsDTO>> getSupportSettings() {
        AdminSupportSettingsDTO settings = adminSettingsService.getSupportSettings();
        return ResponseEntity.ok(new ApiResponse<>(true, "Support contact settings retrieved successfully", settings));
    }

    @PutMapping("/support")
    public ResponseEntity<ApiResponse<AdminSupportSettingsDTO>> updateSupportSettings(
            @Valid @RequestBody AdminSupportSettingsDTO request) {
        AdminSupportSettingsDTO updated = adminSettingsService.updateSupportSettings(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Support contact settings updated successfully", updated));
    }

    // 6. System Preferences
    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<AdminSystemPreferencesDTO>> getSystemPreferences() {
        AdminSystemPreferencesDTO preferences = adminSettingsService.getSystemPreferences();
        return ResponseEntity.ok(new ApiResponse<>(true, "System preferences retrieved successfully", preferences));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<AdminSystemPreferencesDTO>> updateSystemPreferences(
            @RequestBody AdminSystemPreferencesDTO request) {
        AdminSystemPreferencesDTO updated = adminSettingsService.updateSystemPreferences(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "System preferences updated successfully", updated));
    }
}
