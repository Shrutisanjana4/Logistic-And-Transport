package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.entity.SupportContact;
import com.primeroute.logistics.entity.SystemSetting;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.repository.SupportContactRepository;
import com.primeroute.logistics.repository.SystemSettingRepository;
import com.primeroute.logistics.repository.UserRepository;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminSettingsService {

    private final UserRepository userRepository;
    private final SupportContactRepository supportContactRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public AdminSettingsService(
            UserRepository userRepository,
            SupportContactRepository supportContactRepository,
            SystemSettingRepository systemSettingRepository,
            PasswordEncoder passwordEncoder,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.supportContactRepository = supportContactRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    // ==========================================
    // 1. ADMIN PROFILE
    // ==========================================
    @Transactional(readOnly = true)
    public AdminProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin user not found with email: " + email));

        AdminProfileDTO dto = new AdminProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole() != null ? user.getRole().name() : "ADMIN");
        dto.setStatus(user.getStatus() != null ? user.getStatus() : "ACTIVE");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastPasswordChange(user.getLastPasswordChange());
        dto.setTwoFactorEnabled(user.getTwoFactorEnabled());
        return dto;
    }

    @Transactional
    public AdminProfileDTO updateProfile(String email, AdminProfileUpdateRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin user not found with email: " + email));

        if (req.getFullName() != null && !req.getFullName().trim().isEmpty()) {
            user.setFullName(req.getFullName().trim());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone().trim());
        }

        userRepository.save(user);

        notificationService.createNotification(
                user,
                "Admin Profile Updated",
                "Your administrative profile information was successfully updated.",
                "SECURITY"
        );

        return getProfile(email);
    }

    // ==========================================
    // 2. SECURITY (CHANGE PASSWORD)
    // ==========================================
    @Transactional
    public void changePassword(String email, AdminSecurityChangePasswordRequest req) {
        if (req.getCurrentPassword() == null || req.getCurrentPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Current password is required.");
        }

        if (req.getNewPassword() == null || req.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required.");
        }

        if (req.getNewPassword().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        if (req.getConfirmPassword() == null || !req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with email: " + email));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password.");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);

        notificationService.createNotification(
                user,
                "Admin Security Alert",
                "Your administrator account password was changed successfully.",
                "SECURITY"
        );
    }

    // ==========================================
    // 3. NOTIFICATION PREFERENCES
    // ==========================================
    @Transactional(readOnly = true)
    public AdminNotificationSettingsDTO getNotificationSettings() {
        AdminNotificationSettingsDTO dto = new AdminNotificationSettingsDTO();
        dto.setEmailNotifications(getBoolSetting("notify_email", true));
        dto.setOrderUpdates(getBoolSetting("notify_order_updates", true));
        dto.setShipmentUpdates(getBoolSetting("notify_shipment_updates", true));
        dto.setDeliveryUpdates(getBoolSetting("notify_delivery_updates", true));
        dto.setInvoiceNotifications(getBoolSetting("notify_invoice", true));
        dto.setSupportTicketNotifications(getBoolSetting("notify_support_tickets", true));
        return dto;
    }

    @Transactional
    public AdminNotificationSettingsDTO updateNotificationSettings(AdminNotificationSettingsDTO req) {
        saveSetting("notify_email", String.valueOf(req.isEmailNotifications()), "NOTIFICATIONS");
        saveSetting("notify_order_updates", String.valueOf(req.isOrderUpdates()), "NOTIFICATIONS");
        saveSetting("notify_shipment_updates", String.valueOf(req.isShipmentUpdates()), "NOTIFICATIONS");
        saveSetting("notify_delivery_updates", String.valueOf(req.isDeliveryUpdates()), "NOTIFICATIONS");
        saveSetting("notify_invoice", String.valueOf(req.isInvoiceNotifications()), "NOTIFICATIONS");
        saveSetting("notify_support_tickets", String.valueOf(req.isSupportTicketNotifications()), "NOTIFICATIONS");
        return getNotificationSettings();
    }

    // ==========================================
    // 4. COMPANY / ORGANIZATION SETTINGS
    // ==========================================
    @Transactional(readOnly = true)
    public AdminCompanySettingsDTO getCompanySettings() {
        AdminCompanySettingsDTO dto = new AdminCompanySettingsDTO();
        dto.setCompanyName(getSetting("company_name", "PrimeRoute Logistics India Pvt. Ltd."));
        dto.setTagline(getSetting("company_tagline", "Smart Logistics. Reliable Delivery."));
        dto.setGstin(getSetting("company_gstin", "29AAAAA0000A1Z5"));
        dto.setCorporateEmail(getSetting("company_corporate_email", "corporate@primeroute.com"));
        dto.setCorporatePhone(getSetting("company_corporate_phone", "+91 80 4567 8900"));
        dto.setWebsite(getSetting("company_website", "https://primeroute.com"));
        dto.setAddress(getSetting("company_address", "Plot 42, Tech Logistics Park, Whitefield"));
        dto.setCity(getSetting("company_city", "Bengaluru"));
        dto.setState(getSetting("company_state", "Karnataka"));
        dto.setPostalCode(getSetting("company_postal_code", "560066"));
        dto.setCountry(getSetting("company_country", "India"));
        return dto;
    }

    @Transactional
    public AdminCompanySettingsDTO updateCompanySettings(AdminCompanySettingsDTO req) {
        saveSetting("company_name", req.getCompanyName(), "COMPANY");
        saveSetting("company_tagline", req.getTagline(), "COMPANY");
        saveSetting("company_gstin", req.getGstin(), "COMPANY");
        saveSetting("company_corporate_email", req.getCorporateEmail(), "COMPANY");
        saveSetting("company_corporate_phone", req.getCorporatePhone(), "COMPANY");
        saveSetting("company_website", req.getWebsite(), "COMPANY");
        saveSetting("company_address", req.getAddress(), "COMPANY");
        saveSetting("company_city", req.getCity(), "COMPANY");
        saveSetting("company_state", req.getState(), "COMPANY");
        saveSetting("company_postal_code", req.getPostalCode(), "COMPANY");
        saveSetting("company_country", req.getCountry(), "COMPANY");
        return getCompanySettings();
    }

    // ==========================================
    // 5. SUPPORT & CONTACT SETTINGS (reusing support_contacts table)
    // ==========================================
    @Transactional(readOnly = true)
    public AdminSupportSettingsDTO getSupportSettings() {
        return supportContactRepository.findAll().stream().findFirst()
                .map(sc -> new AdminSupportSettingsDTO(
                        sc.getSupportEmail(),
                        sc.getSupportPhone(),
                        sc.getAvailability(),
                        sc.getDescription()
                ))
                .orElseGet(() -> new AdminSupportSettingsDTO(
                        "support@primeroute.com",
                        "+91 98765 43210",
                        "24/7 Dedicated Logistics Assistance",
                        "Operations desk hotline for critical freight inquiries and routing delays."
                ));
    }

    @Transactional
    public AdminSupportSettingsDTO updateSupportSettings(AdminSupportSettingsDTO req) {
        SupportContact contact = supportContactRepository.findAll().stream().findFirst()
                .orElseGet(() -> new SupportContact(
                        req.getSupportEmail(),
                        req.getSupportPhone(),
                        req.getAvailability(),
                        req.getDescription()
                ));

        contact.setSupportEmail(req.getSupportEmail().trim());
        contact.setSupportPhone(req.getSupportPhone().trim());
        contact.setAvailability(req.getAvailability().trim());
        contact.setDescription(req.getDescription() != null ? req.getDescription().trim() : "");
        contact.setUpdatedAt(LocalDateTime.now());

        supportContactRepository.save(contact);

        return new AdminSupportSettingsDTO(
                contact.getSupportEmail(),
                contact.getSupportPhone(),
                contact.getAvailability(),
                contact.getDescription()
        );
    }

    // ==========================================
    // 6. SYSTEM PREFERENCES
    // ==========================================
    @Transactional(readOnly = true)
    public AdminSystemPreferencesDTO getSystemPreferences() {
        AdminSystemPreferencesDTO dto = new AdminSystemPreferencesDTO();
        dto.setDefaultCurrency(getSetting("pref_currency", "INR (₹)"));
        dto.setDefaultDeliveryType(getSetting("pref_delivery_type", "EXPRESS"));
        dto.setDateFormat(getSetting("pref_date_format", "DD/MM/YYYY"));
        dto.setTimeFormat(getSetting("pref_time_format", "12-hour (hh:mm A)"));
        dto.setTimeZone(getSetting("pref_time_zone", "Asia/Kolkata (IST)"));
        return dto;
    }

    @Transactional
    public AdminSystemPreferencesDTO updateSystemPreferences(AdminSystemPreferencesDTO req) {
        saveSetting("pref_currency", req.getDefaultCurrency(), "SYSTEM");
        saveSetting("pref_delivery_type", req.getDefaultDeliveryType(), "SYSTEM");
        saveSetting("pref_date_format", req.getDateFormat(), "SYSTEM");
        saveSetting("pref_time_format", req.getTimeFormat(), "SYSTEM");
        saveSetting("pref_time_zone", req.getTimeZone(), "SYSTEM");
        return getSystemPreferences();
    }

    // Helpers
    private String getSetting(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    private boolean getBoolSetting(String key, boolean defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(s -> "true".equalsIgnoreCase(s.getSettingValue()))
                .orElse(defaultValue);
    }

    private void saveSetting(String key, String value, String category) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseGet(() -> new SystemSetting(key, value, category));
        setting.setSettingValue(value != null ? value : "");
        setting.setCategory(category);
        setting.setUpdatedAt(LocalDateTime.now());
        systemSettingRepository.save(setting);
    }
}
