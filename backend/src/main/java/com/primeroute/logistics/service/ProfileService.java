package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.ChangePasswordRequest;
import com.primeroute.logistics.dto.ProfileDTO;
import com.primeroute.logistics.dto.ProfileUpdateRequest;
import com.primeroute.logistics.entity.Shipment;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.repository.OrderRepository;
import com.primeroute.logistics.repository.ShipmentRepository;
import com.primeroute.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Transactional(readOnly = true)
    public ProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        long totalOrders = orderRepository.countByCustomer(user);

        List<Shipment> shipments = shipmentRepository.findByOrder_CustomerOrderByCreatedAtDesc(user);
        long activeShipments = shipments.stream()
                .filter(s -> s.getStatus() != null
                        && !"DELIVERED".equalsIgnoreCase(s.getStatus().name())
                        && !"CANCELLED".equalsIgnoreCase(s.getStatus().name()))
                .count();

        ProfileDTO dto = new ProfileDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole() != null ? user.getRole().name() : "CUSTOMER");
        dto.setCompanyName(user.getCompanyName());
        dto.setBusinessType(user.getBusinessType());
        dto.setBusinessEmail(user.getBusinessEmail());
        dto.setBusinessPhone(user.getBusinessPhone());
        dto.setRegistrationNumber(user.getRegistrationNumber());
        dto.setGstin(user.getGstin());
        dto.setBusinessAddress(user.getBusinessAddress());
        dto.setVerificationStatus(user.getVerificationStatus() != null ? user.getVerificationStatus() : "PENDING");
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastPasswordChange(user.getLastPasswordChange());
        dto.setTwoFactorEnabled(user.getTwoFactorEnabled());
        dto.setTotalOrders(totalOrders);
        dto.setActiveShipments(activeShipments);

        return dto;
    }

    @Transactional
    public ProfileDTO updateProfile(String email, ProfileUpdateRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (req.getFullName() != null && !req.getFullName().trim().isEmpty()) {
            user.setFullName(req.getFullName().trim());
        }
        if (req.getPhone() != null) {
            user.setPhone(req.getPhone().trim());
        }
        if (req.getCompanyName() != null) {
            user.setCompanyName(req.getCompanyName().trim());
        }
        if (req.getBusinessType() != null) {
            user.setBusinessType(req.getBusinessType().trim());
        }
        if (req.getBusinessEmail() != null) {
            user.setBusinessEmail(req.getBusinessEmail().trim());
        }
        if (req.getBusinessPhone() != null) {
            user.setBusinessPhone(req.getBusinessPhone().trim());
        }
        if (req.getRegistrationNumber() != null) {
            user.setRegistrationNumber(req.getRegistrationNumber().trim());
        }
        if (req.getGstin() != null) {
            user.setGstin(req.getGstin().trim());
        }
        if (req.getBusinessAddress() != null) {
            user.setBusinessAddress(req.getBusinessAddress().trim());
        }

        userRepository.save(user);

        notificationService.createNotification(
                user,
                "Profile Updated",
                "Your business profile details were successfully updated.",
                "SECURITY"
        );

        return getProfile(email);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (req.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);

        notificationService.createNotification(
                user,
                "Password Changed",
                "Your account password was updated successfully. If this wasn't you, please contact support immediately.",
                "SECURITY"
        );
    }
}
