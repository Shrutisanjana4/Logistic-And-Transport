package com.primeroute.logistics.dto;

import com.primeroute.logistics.entity.DriverStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreateDriverRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Pattern(
        regexp = "^(?!\\d+$)[a-zA-Z0-9\\s.'-]+$",
        message = "Full name must contain letters and cannot be purely numeric"
    )
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "Email must be valid"
    )
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^(?:\\+91[\\-\\s]?)?[6-9]\\d{9}$",
        message = "Please provide a valid 10-digit Indian mobile number (e.g., +91 9876543210 or 9876543210)"
    )
    private String phone;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotNull(message = "License expiry date is required")
    @FutureOrPresent(message = "Driver license expiry date must not be in the past")
    private LocalDate licenseExpiryDate;

    private String address;

    private DriverStatus status = DriverStatus.AVAILABLE;
    private Long warehouseId;
    private Long vehicleId;

    private String password;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public CreateDriverRequest() {}

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
}
