package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.AddressRequest;
import com.primeroute.logistics.dto.AddressResponse;
import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.service.AddressService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        AddressResponse response = addressService.createAddress(email, request);

        ApiResponse<AddressResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Address created successfully",
                        response
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAllAddresses(
            Authentication authentication) {

        String email = authentication.getName();

        List<AddressResponse> addresses =
                addressService.getAllAddresses(email);

        ApiResponse<List<AddressResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        "Addresses retrieved successfully",
                        addresses
                );

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        AddressResponse response =
                addressService.getAddressById(email, id);

        ApiResponse<AddressResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Address retrieved successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        AddressResponse response =
                addressService.updateAddress(email, id, request);

        ApiResponse<AddressResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Address updated successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        addressService.deleteAddress(email, id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        "Address deleted successfully",
                        null
                );

        return ResponseEntity.ok(apiResponse);
    }
}