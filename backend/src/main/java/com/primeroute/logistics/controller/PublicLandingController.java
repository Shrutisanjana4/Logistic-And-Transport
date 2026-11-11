package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.PublicContactInfoDTO;
import com.primeroute.logistics.dto.PublicFreightInquiryRequest;
import com.primeroute.logistics.dto.PublicFreightInquiryResponse;
import com.primeroute.logistics.dto.PublicStatisticsDTO;
import com.primeroute.logistics.dto.TrackingResponseDTO;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.repository.*;
import com.primeroute.logistics.service.FreightInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public")
public class PublicLandingController {

    private final UserRepository userRepository;
    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingEventRepository eventRepository;
    private final SupportContactRepository supportContactRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final FreightInquiryService freightInquiryService;

    public PublicLandingController(
            UserRepository userRepository,
            ShipmentRepository shipmentRepository,
            ShipmentTrackingEventRepository eventRepository,
            SupportContactRepository supportContactRepository,
            SystemSettingRepository systemSettingRepository,
            FreightInquiryService freightInquiryService) {
        this.userRepository = userRepository;
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.supportContactRepository = supportContactRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.freightInquiryService = freightInquiryService;
    }

    /**
     * Aggregated statistics directly calculated from PostgreSQL
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<PublicStatisticsDTO>> getPublicStatistics() {
        long activeCustomers = userRepository.countByRole(Role.CUSTOMER);
        long shipmentsManaged = shipmentRepository.count();
        long deliveredShipments = shipmentRepository.countByStatus(OrderStatus.DELIVERED);
        long inTransitShipments = shipmentRepository.countByStatus(OrderStatus.IN_TRANSIT);

        String availability = "24/7 Dedicated Logistics Assistance";
        List<SupportContact> contacts = supportContactRepository.findAll();
        if (!contacts.isEmpty() && contacts.get(0).getAvailability() != null && !contacts.get(0).getAvailability().isBlank()) {
            availability = contacts.get(0).getAvailability();
        }

        PublicStatisticsDTO dto = new PublicStatisticsDTO(
                activeCustomers,
                shipmentsManaged,
                deliveredShipments,
                inTransitShipments,
                availability
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Public statistics retrieved successfully", dto));
    }

    /**
     * Authoritative support contact and company profile info
     */
    @GetMapping("/contact-info")
    public ResponseEntity<ApiResponse<PublicContactInfoDTO>> getPublicContactInfo() {
        String supportEmail = "support@primeroute.com";
        String supportPhone = "+91 80 4567 8900";
        String availability = "24/7 Dedicated Logistics Assistance";
        String description = "PrimeRoute customer care & freight escalation hotline.";

        List<SupportContact> contacts = supportContactRepository.findAll();
        if (!contacts.isEmpty()) {
            SupportContact sc = contacts.get(0);
            if (sc.getSupportEmail() != null && !sc.getSupportEmail().isBlank()) supportEmail = sc.getSupportEmail();
            if (sc.getSupportPhone() != null && !sc.getSupportPhone().isBlank()) supportPhone = sc.getSupportPhone();
            if (sc.getAvailability() != null && !sc.getAvailability().isBlank()) availability = sc.getAvailability();
            if (sc.getDescription() != null && !sc.getDescription().isBlank()) description = sc.getDescription();
        }

        String companyName = getSetting("company_name", "PrimeRoute Logistics India Pvt. Ltd.");
        String companyAddress = getSetting("company_address", "Plot 42, Tech Logistics Park, Whitefield");
        String city = getSetting("company_city", "Bengaluru");
        String state = getSetting("company_state", "Karnataka");
        String country = getSetting("company_country", "India");
        String postalCode = getSetting("company_postal_code", "560066");
        String website = getSetting("company_website", "https://primeroute.com");

        PublicContactInfoDTO dto = new PublicContactInfoDTO(
                supportEmail,
                supportPhone,
                availability,
                description,
                companyName,
                companyAddress,
                city,
                state,
                country,
                postalCode,
                website
        );

        return ResponseEntity.ok(new ApiResponse<>(true, "Public contact info retrieved successfully", dto));
    }

    /**
     * Safe public shipment tracking by tracking number or order number.
     * Excludes private customer data (phone, email, billing, passwords).
     */
    @GetMapping("/tracking/{identifier}")
    public ResponseEntity<ApiResponse<TrackingResponseDTO>> trackPublicShipment(@PathVariable String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(false, "Please enter a tracking or waybill number.", null)
            );
        }

        String query = identifier.trim();
        String cleanQuery = query.toUpperCase().startsWith("TRK-") ? query.substring(4) : query;

        Optional<Shipment> optShipment = shipmentRepository.findByTrackingNumber(query);
        if (optShipment.isEmpty()) {
            optShipment = shipmentRepository.findByOrder_OrderNumber(query);
        }
        if (optShipment.isEmpty()) {
            optShipment = shipmentRepository.findByTrackingNumber("TRK-" + cleanQuery);
        }
        if (optShipment.isEmpty()) {
            optShipment = shipmentRepository.findByOrder_OrderNumber(cleanQuery);
        }

        if (optShipment.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "We couldn't find a shipment with that tracking number.", null)
            );
        }

        Shipment shipment = optShipment.get();
        Order order = shipment.getOrder();
        Address pickup = order != null ? order.getPickupAddress() : null;
        Address delivery = order != null ? order.getDeliveryAddress() : null;

        TrackingResponseDTO dto = new TrackingResponseDTO();
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setOrderNumber(order != null ? order.getOrderNumber() : "");
        dto.setStatus(shipment.getStatus() != null ? shipment.getStatus().name() : "PENDING");
        dto.setDeliveryType(order != null && order.getDeliveryType() != null ? order.getDeliveryType().name() : "STANDARD");
        dto.setEstimatedDeliveryDate(shipment.getEstimatedDeliveryDate());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setLastUpdatedAt(shipment.getUpdatedAt());

        // Safe pickup location (exclude personal contact info for public view)
        if (pickup != null) {
            dto.setPickup(new TrackingResponseDTO.LocationDTO(
                    "", // omit personal contact name for privacy
                    "", // omit personal phone for privacy
                    (pickup.getAddressLine1() != null ? pickup.getAddressLine1() : "") + ", " + pickup.getCity() + ", " + pickup.getState(),
                    pickup.getCity(),
                    pickup.getState(),
                    pickup.getPostalCode(),
                    pickup.getCountry(),
                    pickup.getLatitude(),
                    pickup.getLongitude()
            ));
        }

        // Safe delivery location (exclude personal contact info for public view)
        if (delivery != null) {
            dto.setDelivery(new TrackingResponseDTO.LocationDTO(
                    "", // omit personal contact name
                    "", // omit personal phone
                    (delivery.getAddressLine1() != null ? delivery.getAddressLine1() : "") + ", " + delivery.getCity() + ", " + delivery.getState(),
                    delivery.getCity(),
                    delivery.getState(),
                    delivery.getPostalCode(),
                    delivery.getCountry(),
                    delivery.getLatitude(),
                    delivery.getLongitude()
            ));
        }

        // Current location
        dto.setCurrentLocation(new TrackingResponseDTO.CurrentLocationDTO(
                shipment.getCurrentLocationName() != null ? shipment.getCurrentLocationName() : (pickup != null ? pickup.getCity() + " Logistics Hub" : "Origin Hub"),
                shipment.getCurrentLatitude(),
                shipment.getCurrentLongitude(),
                shipment.getUpdatedAt() != null ? shipment.getUpdatedAt() : LocalDateTime.now()
        ));

        // Package info
        if (order != null) {
            dto.setPackageDetails(new TrackingResponseDTO.PackageDTO(
                    order.getPackageDescription() != null ? order.getPackageDescription() : "Commercial Consignment",
                    order.getPackageWeight() != null ? order.getPackageWeight() : 0.0,
                    order.getPackageQuantity() != null ? order.getPackageQuantity() : 1
            ));
        }

        // Tracking milestone events
        List<ShipmentTrackingEvent> events = eventRepository.findByShipmentOrderByEventTimeAsc(shipment);
        List<TrackingResponseDTO.TrackingEventDTO> eventDTOs = new ArrayList<>();
        OrderStatus status = shipment.getStatus();

        for (ShipmentTrackingEvent e : events) {
            boolean isCompleted = isEventCompleted(e.getStatus(), status);
            boolean isCurrent = isEventCurrent(e.getStatus(), status);
            eventDTOs.add(new TrackingResponseDTO.TrackingEventDTO(
                    e.getId(),
                    e.getStatus(),
                    e.getTitle(),
                    e.getDescription(),
                    e.getLocationName(),
                    e.getLatitude(),
                    e.getLongitude(),
                    e.getEventTime(),
                    isCompleted,
                    isCurrent
            ));
        }
        dto.setEvents(eventDTOs);

        return ResponseEntity.ok(new ApiResponse<>(true, "Shipment tracking retrieved successfully", dto));
    }

    /**
     * Submit a public freight quotation request.
     */
    @PostMapping(path = {"/quotes", "/quote"})
    public ResponseEntity<ApiResponse<PublicFreightInquiryResponse>> submitFreightQuote(
            @Valid @RequestBody PublicFreightInquiryRequest request) {
        request.setInquiryType("QUOTATION");
        PublicFreightInquiryResponse response = freightInquiryService.processInquiry(request, "QUOTATION");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, response.getMessage(), response)
        );
    }

    /**
     * Submit a public freight consultation or general inquiry.
     */
    @PostMapping(path = {"/inquiries", "/inquiry", "/consultations", "/consultation", "/freight-inquiry"})
    public ResponseEntity<ApiResponse<PublicFreightInquiryResponse>> submitFreightInquiry(
            @Valid @RequestBody PublicFreightInquiryRequest request) {
        if (request.getInquiryType() == null || request.getInquiryType().isBlank()) {
            request.setInquiryType("CONSULTATION");
        }
        PublicFreightInquiryResponse response = freightInquiryService.processInquiry(request, "CONSULTATION");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, response.getMessage(), response)
        );
    }

    private String getSetting(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .filter(v -> !v.isBlank())
                .orElse(defaultValue);
    }

    private boolean isEventCompleted(String eventStatus, OrderStatus currentStatus) {
        int eventRank = getStatusRank(eventStatus);
        int currentRank = getStatusRank(currentStatus != null ? currentStatus.name() : "");
        return currentRank >= eventRank;
    }

    private boolean isEventCurrent(String eventStatus, OrderStatus currentStatus) {
        return getStatusRank(eventStatus) == getStatusRank(currentStatus != null ? currentStatus.name() : "");
    }

    private int getStatusRank(String status) {
        if (status == null) return 0;
        return switch (status.toUpperCase()) {
            case "ORDER_CREATED", "PENDING", "CONFIRMED" -> 1;
            case "PICKED_UP" -> 2;
            case "IN_TRANSIT" -> 3;
            case "OUT_FOR_DELIVERY" -> 4;
            case "DELIVERED" -> 5;
            default -> 0;
        };
    }
}
