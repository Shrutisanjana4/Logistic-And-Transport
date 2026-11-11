package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.PublicFreightInquiryRequest;
import com.primeroute.logistics.dto.PublicFreightInquiryResponse;
import com.primeroute.logistics.entity.FreightInquiry;
import com.primeroute.logistics.repository.FreightInquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class FreightInquiryService {

    private static final List<String> ALLOWED_SERVICE_TYPES = Arrays.asList(
            "Road Freight (FTL)",
            "Part Load (LTL)",
            "Express Priority Linehaul",
            "Warehousing & Cross-Docking",
            "Last-Mile Delivery",
            "Cold Chain Freight",
            "Intermodal Rail-Road",
            "Full Truckload (FTL)",
            "Less Than Truckload (LTL)",
            "Freight Consultation"
    );

    private final FreightInquiryRepository inquiryRepository;
    private final SecureRandom random = new SecureRandom();

    public FreightInquiryService(FreightInquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    public PublicFreightInquiryResponse processInquiry(PublicFreightInquiryRequest request, String defaultType) {
        if (request == null) {
            throw new IllegalArgumentException("Inquiry request body cannot be null.");
        }

        // 1. Determine & validate inquiry type
        String type = request.getInquiryType();
        if (type == null || type.trim().isEmpty()) {
            type = defaultType != null ? defaultType : "CONSULTATION";
        }
        type = type.trim().toUpperCase(Locale.ROOT);
        if (!type.equals("QUOTATION") && !type.equals("CONSULTATION")) {
            type = "CONSULTATION";
        }

        // 2. Validate Service Type against Allowed Whitelist
        String serviceType = request.getServiceType();
        if (serviceType == null || serviceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid service type.");
        }
        String matchedServiceType = matchAllowedServiceType(serviceType.trim());
        if (matchedServiceType == null) {
            throw new IllegalArgumentException("Invalid service type '" + serviceType + "'. Please select a valid logistics service.");
        }

        // 3. Validate Cargo Weight
        Double weight = request.getWeight();
        if (weight != null) {
            if (Double.isNaN(weight) || Double.isInfinite(weight) || weight <= 0.0) {
                throw new IllegalArgumentException("Enter a valid cargo weight greater than 0.");
            }
            if (weight > 100000.0) {
                throw new IllegalArgumentException("Cargo weight exceeds maximum allowed capacity (100,000 kg).");
            }
        }

        // 4. Validate Additional Message
        String message = request.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            String trimmedMessage = message.trim();
            if (trimmedMessage.length() > 1000) {
                throw new IllegalArgumentException("Additional specifications must not exceed 1000 characters.");
            }
            if (trimmedMessage.length() >= 3 && !trimmedMessage.matches(".*[a-zA-Z0-9].*")) {
                throw new IllegalArgumentException("Additional specifications must contain meaningful text, not only symbols.");
            }
        }

        // 5. Generate Unique Reference Number
        String inquiryNumber = generateInquiryNumber();

        // 6. Persist to PostgreSQL
        FreightInquiry inquiry = new FreightInquiry();
        inquiry.setInquiryNumber(inquiryNumber);
        inquiry.setInquiryType(type);
        inquiry.setCompanyName(request.getCompanyName().trim());
        inquiry.setContactName(request.getContactName().trim());
        inquiry.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        inquiry.setPhone(request.getPhone().trim());
        inquiry.setOrigin(request.getOrigin().trim());
        inquiry.setDestination(request.getDestination().trim());
        inquiry.setServiceType(matchedServiceType);
        inquiry.setWeight(weight);
        inquiry.setCargoType(request.getCargoType() != null ? request.getCargoType().trim() : "Industrial Freight");
        inquiry.setMessage(message != null ? message.trim() : null);
        inquiry.setStatus("NEW");
        inquiry.setCreatedAt(LocalDateTime.now());
        inquiry.setUpdatedAt(LocalDateTime.now());

        FreightInquiry saved = inquiryRepository.save(inquiry);

        // 7. Format Response
        String responseMessage = type.equals("QUOTATION")
                ? "Quotation Request Received"
                : "Inquiry Submitted Successfully";

        return new PublicFreightInquiryResponse(
                saved.getInquiryNumber(),
                saved.getInquiryType(),
                saved.getCompanyName(),
                saved.getContactName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getOrigin(),
                saved.getDestination(),
                saved.getServiceType(),
                saved.getWeight(),
                saved.getStatus(),
                saved.getCreatedAt(),
                responseMessage
        );
    }

    private String matchAllowedServiceType(String input) {
        for (String allowed : ALLOWED_SERVICE_TYPES) {
            if (allowed.equalsIgnoreCase(input)) {
                return allowed;
            }
        }
        return null;
    }

    private String generateInquiryNumber() {
        int currentYear = Year.now().getValue();
        for (int attempt = 0; attempt < 10; attempt++) {
            int num = 10000 + random.nextInt(90000);
            String candidate = "INQ-PR-" + currentYear + "-" + num;
            if (!inquiryRepository.existsByInquiryNumber(candidate)) {
                return candidate;
            }
        }
        return "INQ-PR-" + currentYear + "-" + System.currentTimeMillis() % 100000;
    }
}
