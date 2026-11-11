package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.AdminDriverDTO;
import com.primeroute.logistics.dto.CreateDriverRequest;
import com.primeroute.logistics.dto.UpdateDriverRequest;
import com.primeroute.logistics.entity.Driver;
import com.primeroute.logistics.entity.DriverStatus;
import com.primeroute.logistics.entity.Role;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.primeroute.logistics.entity.OrderStatus;
import com.primeroute.logistics.entity.Shipment;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.DriverRepository;
import com.primeroute.logistics.repository.VehicleRepository;
import com.primeroute.logistics.repository.WarehouseRepository;
import com.primeroute.logistics.entity.Vehicle;
import com.primeroute.logistics.entity.VehicleStatus;
import com.primeroute.logistics.entity.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDriverService {

    private final DriverRepository driverRepository;
    private final com.primeroute.logistics.repository.ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WarehouseRepository warehouseRepository;
    private final VehicleRepository vehicleRepository;

    public AdminDriverService(
            DriverRepository driverRepository,
            com.primeroute.logistics.repository.ShipmentRepository shipmentRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            WarehouseRepository warehouseRepository,
            VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.warehouseRepository = warehouseRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Normalize and validate Indian mobile phone number:
     * Accepts 10 digits starting with 6-9, optionally prefixed by +91, 91, or 0.
     * Normalized format stored in DB: +91 9876543210
     */
    public static String normalizeIndianPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        String cleaned = phone.trim().replaceAll("[\\s\\-()]", "");
        if (cleaned.startsWith("+91")) {
            cleaned = cleaned.substring(3);
        } else if (cleaned.startsWith("91") && cleaned.length() == 12) {
            cleaned = cleaned.substring(2);
        } else if (cleaned.startsWith("0") && cleaned.length() == 11) {
            cleaned = cleaned.substring(1);
        }
        if (!cleaned.matches("^[6-9]\\d{9}$")) {
            throw new IllegalArgumentException("Invalid Indian mobile number. Please enter a valid 10-digit number (e.g., +91 9876543210 or 9876543210).");
        }
        return "+91 " + cleaned;
    }

    @Transactional
    public List<AdminDriverDTO> getAllDrivers() {
        List<Driver> drivers = driverRepository.findAllByOrderByCreatedAtDesc();
        return drivers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminDriverDTO> getAvailableDrivers() {
        List<Driver> available = driverRepository.findAvailableDrivers();
        return available.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminDriverDTO getDriverById(Long id) {
        Driver driver = driverRepository.findByIdWithShipments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));
        return convertToDto(driver);
    }

    @Transactional
    public AdminDriverDTO createDriver(CreateDriverRequest request) {
        String formattedPhone = normalizeIndianPhone(request.getPhone());
        String license = request.getLicenseNumber() != null ? request.getLicenseNumber().trim().toUpperCase() : "";

        if (license.isBlank()) {
            throw new IllegalArgumentException("License number is required.");
        }

        if (driverRepository.existsByLicenseNumber(license)) {
            throw new IllegalArgumentException("A driver with license number " + license + " already exists.");
        }

        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email address is required.");
        }

        if (driverRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("A driver with email " + email + " already exists.");
        }

        if (request.getLicenseExpiryDate() == null) {
            throw new IllegalArgumentException("License expiry date is required.");
        }

        Driver driver = new Driver();
        driver.setFullName(request.getFullName().trim());
        driver.setEmail(email);
        driver.setPhone(formattedPhone);
        driver.setLicenseNumber(license);
        driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        driver.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        if (request.getWarehouseId() != null) {
            driver.setWarehouse(warehouseRepository.findById(request.getWarehouseId()).orElse(null));
        } else {
            driver.setWarehouse(null);
        }
        driver.setStatus(request.getStatus() != null ? request.getStatus() : DriverStatus.AVAILABLE);
        if (request.getWarehouseId() != null) {
            warehouseRepository.findById(request.getWarehouseId()).ifPresent(driver::setWarehouse);
        }

        Driver saved = driverRepository.save(driver);

        if (request.getVehicleId() != null) {
            Vehicle targetVehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

            if (targetVehicle.getStatus() == VehicleStatus.MAINTENANCE || targetVehicle.getStatus() == VehicleStatus.INACTIVE || targetVehicle.getStatus() == VehicleStatus.IN_TRANSIT) {
                throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is currently " + targetVehicle.getStatus() + " and cannot be assigned.");
            }

            if (saved.getWarehouse() != null) {
                if (targetVehicle.getWarehouse() != null && !targetVehicle.getWarehouse().getId().equals(saved.getWarehouse().getId())) {
                    throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " belongs to warehouse " + targetVehicle.getWarehouse().getWarehouseName() + " and cannot be assigned to a driver in " + saved.getWarehouse().getWarehouseName() + ".");
                } else if (targetVehicle.getWarehouse() == null) {
                    targetVehicle.setWarehouse(saved.getWarehouse());
                }
            } else if (targetVehicle.getWarehouse() != null) {
                throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " belongs to warehouse " + targetVehicle.getWarehouse().getWarehouseName() + " and cannot be assigned to an unassigned (company-wide) driver.");
            }

            if (targetVehicle.getAssignedDriver() != null && !targetVehicle.getAssignedDriver().getId().equals(saved.getId())) {
                throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is already assigned to driver " + targetVehicle.getAssignedDriver().getFullName() + ".");
            }

            List<Shipment> targetActive = shipmentRepository.findActiveShipmentsByVehicle(targetVehicle);
            if (!targetActive.isEmpty()) {
                throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is currently engaged in active shipment " + targetActive.get(0).getTrackingNumber() + " and cannot be assigned.");
            }

            targetVehicle.setAssignedDriver(saved);
            vehicleRepository.save(targetVehicle);
        }

        // Ensure matching User entity for driver portal authentication
        if (!userRepository.existsByEmailIgnoreCase(email) && !userRepository.existsByEmail(email)) {
            String rawPass = (request.getPassword() != null && !request.getPassword().isBlank())
                    ? request.getPassword().trim()
                    : "Driver@2026";
            User user = new User(
                    email,
                    passwordEncoder.encode(rawPass),
                    saved.getFullName(),
                    Role.DRIVER
            );
            user.setPhone(formattedPhone);
            userRepository.save(user);
        }

        return convertToDto(saved);
    }

    @Transactional
    public AdminDriverDTO updateDriver(Long id, UpdateDriverRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));

        String formattedPhone = normalizeIndianPhone(request.getPhone());
        String newLicense = request.getLicenseNumber() != null ? request.getLicenseNumber().trim().toUpperCase() : "";

        if (newLicense.isBlank()) {
            throw new IllegalArgumentException("License number is required.");
        }

        if (!newLicense.equalsIgnoreCase(driver.getLicenseNumber()) && driverRepository.existsByLicenseNumber(newLicense)) {
            throw new IllegalArgumentException("License number " + newLicense + " is already registered to another driver.");
        }

        String newEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (newEmail.isBlank()) {
            throw new IllegalArgumentException("Email address is required.");
        }

        if (!newEmail.equalsIgnoreCase(driver.getEmail()) && driverRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email " + newEmail + " is already registered to another driver.");
        }

        if (request.getLicenseExpiryDate() == null) {
            throw new IllegalArgumentException("License expiry date is required.");
        }

        driver.setFullName(request.getFullName().trim());
        driver.setEmail(newEmail);
        driver.setPhone(formattedPhone);
        driver.setLicenseNumber(newLicense);
        driver.setLicenseExpiryDate(request.getLicenseExpiryDate());
        driver.setAddress(request.getAddress() != null ? request.getAddress().trim() : null);
        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + request.getWarehouseId()));
            driver.setWarehouse(warehouse);
        } else {
            driver.setWarehouse(null);
        }
        if (request.getStatus() != null) {
            List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByDriver(driver);
            if (!activeShipments.isEmpty() && request.getStatus() == DriverStatus.AVAILABLE) {
                throw new IllegalArgumentException("Cannot set driver status to AVAILABLE while assigned to active shipment (" + activeShipments.get(0).getTrackingNumber() + ").");
            }
            driver.setStatus(request.getStatus());
        }

        // Primary Vehicle Assignment & Validation
        List<Vehicle> currentVehicles = vehicleRepository.findByAssignedDriver(driver);
        Vehicle currentVehicle = currentVehicles.isEmpty() ? null : currentVehicles.get(0);

        if (request.getVehicleId() != null) {
            if (currentVehicle == null || !currentVehicle.getId().equals(request.getVehicleId())) {
                // Driver is changing or acquiring a vehicle
                if (currentVehicle != null) {
                    List<Shipment> currentVehActive = shipmentRepository.findActiveShipmentsByVehicle(currentVehicle);
                    if (!currentVehActive.isEmpty()) {
                        throw new IllegalArgumentException("Cannot reassign vehicle: Current vehicle (" + currentVehicle.getVehicleNumber() + ") is currently assigned to an active shipment (" + currentVehActive.get(0).getTrackingNumber() + ").");
                    }
                    currentVehicle.setAssignedDriver(null);
                    vehicleRepository.save(currentVehicle);
                }

                List<Shipment> driverActive = shipmentRepository.findActiveShipmentsByDriver(driver);
                if (!driverActive.isEmpty()) {
                    throw new IllegalArgumentException("Cannot change vehicle assignment while driver is on an active trip (" + driverActive.get(0).getTrackingNumber() + ").");
                }

                Vehicle targetVehicle = vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

                if (targetVehicle.getStatus() == VehicleStatus.MAINTENANCE || targetVehicle.getStatus() == VehicleStatus.INACTIVE || targetVehicle.getStatus() == VehicleStatus.IN_TRANSIT) {
                    throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is currently " + targetVehicle.getStatus() + " and cannot be assigned.");
                }

                if (driver.getWarehouse() != null) {
                    if (targetVehicle.getWarehouse() != null && !targetVehicle.getWarehouse().getId().equals(driver.getWarehouse().getId())) {
                        throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " belongs to a different warehouse (" + targetVehicle.getWarehouse().getWarehouseName() + ") and cannot be assigned to a driver in " + driver.getWarehouse().getWarehouseName() + ".");
                    } else if (targetVehicle.getWarehouse() == null) {
                        targetVehicle.setWarehouse(driver.getWarehouse());
                    }
                } else if (targetVehicle.getWarehouse() != null) {
                    throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " belongs to warehouse " + targetVehicle.getWarehouse().getWarehouseName() + " and cannot be assigned to an unassigned (company-wide) driver.");
                }

                if (targetVehicle.getAssignedDriver() != null && !targetVehicle.getAssignedDriver().getId().equals(driver.getId())) {
                    throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is already assigned to driver " + targetVehicle.getAssignedDriver().getFullName() + ".");
                }

                List<Shipment> targetActive = shipmentRepository.findActiveShipmentsByVehicle(targetVehicle);
                if (!targetActive.isEmpty()) {
                    throw new IllegalArgumentException("Vehicle " + targetVehicle.getVehicleNumber() + " is currently engaged in active shipment " + targetActive.get(0).getTrackingNumber() + " and cannot be assigned.");
                }

                targetVehicle.setAssignedDriver(driver);
                vehicleRepository.save(targetVehicle);
            }
        } else {
            // Unassign primary vehicle if requested
            if (currentVehicle != null) {
                List<Shipment> currentVehActive = shipmentRepository.findActiveShipmentsByVehicle(currentVehicle);
                if (!currentVehActive.isEmpty()) {
                    throw new IllegalArgumentException("This vehicle (" + currentVehicle.getVehicleNumber() + ") cannot be unassigned while it is assigned to an active shipment (" + currentVehActive.get(0).getTrackingNumber() + ").");
                }
                currentVehicle.setAssignedDriver(null);
                vehicleRepository.save(currentVehicle);
            }
        }

        Driver updated = driverRepository.save(driver);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));

        if (driver.getShipments() != null && !driver.getShipments().isEmpty()) {
            boolean hasActiveShipments = driver.getShipments().stream()
                    .anyMatch(s -> s.getStatus() != OrderStatus.DELIVERED && s.getStatus() != OrderStatus.CANCELLED);

            if (hasActiveShipments) {
                throw new IllegalStateException("This driver is currently assigned to an active shipment and cannot be deleted.");
            }

            // Disassociate historical completed shipments before deleting driver
            for (Shipment s : driver.getShipments()) {
                s.setDriver(null);
            }
        }

        driverRepository.delete(driver);
    }

    private AdminDriverDTO convertToDto(Driver driver) {
        AdminDriverDTO dto = new AdminDriverDTO();
        dto.setId(driver.getId());
        dto.setFullName(driver.getFullName());
        dto.setEmail(driver.getEmail());
        dto.setPhone(driver.getPhone());
        dto.setLicenseNumber(driver.getLicenseNumber());
        dto.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        dto.setAddress(driver.getAddress());
        dto.setCreatedAt(driver.getCreatedAt());
        dto.setUpdatedAt(driver.getUpdatedAt());
        if (driver.getWarehouse() != null) {
            dto.setWarehouseId(driver.getWarehouse().getId());
            dto.setWarehouseName(driver.getWarehouse().getWarehouseName());
        }
        List<Vehicle> linkedVehicles = vehicleRepository.findByAssignedDriver(driver);
        if (!linkedVehicles.isEmpty()) {
            dto.setVehicleId(linkedVehicles.get(0).getId());
            dto.setVehicleNumber(linkedVehicles.get(0).getVehicleNumber());
        }

        // Genuinely active shipments count
        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByDriver(driver);
        int activeCount = activeShipments.size();
        dto.setAssignedShipmentsCount(activeCount);

        List<String> activeTrackingNos = activeShipments.stream()
                .map(Shipment::getTrackingNumber)
                .collect(Collectors.toList());
        dto.setAssignedShipmentNumbers(activeTrackingNos);
        if (!activeTrackingNos.isEmpty()) {
            dto.setCurrentShipmentTrackingNumber(activeTrackingNos.get(0));
        }

        // Synchronize status with active shipment reality:
        // 1. If driver is currently assigned to an active shipment, status is ON_TRIP
        // 2. If driver was ON_TRIP but has no active shipments, transition back to AVAILABLE
        // 3. Otherwise, retain the driver's persisted status (AVAILABLE, ON_DUTY, ON_DELIVERY, OFF_DUTY, INACTIVE)
        DriverStatus computedStatus;
        if (activeCount > 0) {
            computedStatus = DriverStatus.ON_TRIP;
        } else if (driver.getStatus() == DriverStatus.ON_TRIP) {
            computedStatus = DriverStatus.AVAILABLE;
        } else {
            computedStatus = driver.getStatus() != null ? driver.getStatus() : DriverStatus.AVAILABLE;
        }
        dto.setStatus(computedStatus);

        // Keep database entity in sync if status drifted
        if (driver.getStatus() != computedStatus) {
            driver.setStatus(computedStatus);
            driverRepository.save(driver);
        }

        return dto;
    }
}
