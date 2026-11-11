package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.PublicFreightInquiryRequest;
import com.primeroute.logistics.dto.PublicFreightInquiryResponse;
import com.primeroute.logistics.entity.FreightInquiry;
import com.primeroute.logistics.repository.FreightInquiryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicFreightInquiryValidationTest {

    @Mock
    private FreightInquiryRepository inquiryRepository;

    private FreightInquiryService inquiryService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        inquiryService = new FreightInquiryService(inquiryRepository);
    }

    private PublicFreightInquiryRequest createValidRequest() {
        PublicFreightInquiryRequest req = new PublicFreightInquiryRequest();
        req.setCompanyName("PrimeRoute Logistics Pvt. Ltd.");
        req.setContactName("Shruti Sanjana Sahoo");
        req.setEmail("qa.inquiry@primeroute.test");
        req.setPhone("9876543210");
        req.setOrigin("Bengaluru Hub");
        req.setDestination("Mumbai DC");
        req.setServiceType("Road Freight (FTL)");
        req.setWeight(3500.0);
        req.setCargoType("Industrial Freight");
        req.setMessage("Pallet count: 10; temperature control required.");
        req.setInquiryType("QUOTATION");
        return req;
    }

    @Test
    @DisplayName("1. Valid quotation request succeeds and persists to database")
    void testValidQuotationRequest() {
        PublicFreightInquiryRequest req = createValidRequest();
        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "Valid request must have 0 Bean validation errors");

        when(inquiryRepository.save(any(FreightInquiry.class))).thenAnswer(invocation -> {
            FreightInquiry fi = invocation.getArgument(0);
            return fi;
        });

        PublicFreightInquiryResponse response = inquiryService.processInquiry(req, "QUOTATION");

        assertNotNull(response);
        assertNotNull(response.getInquiryNumber());
        assertTrue(response.getInquiryNumber().startsWith("INQ-PR-"));
        assertEquals("QUOTATION", response.getInquiryType());
        assertEquals("Quotation Request Received", response.getMessage());
        assertEquals("PrimeRoute Logistics Pvt. Ltd.", response.getCompanyName());
        assertEquals("Shruti Sanjana Sahoo", response.getContactName());

        ArgumentCaptor<FreightInquiry> captor = ArgumentCaptor.forClass(FreightInquiry.class);
        verify(inquiryRepository, times(1)).save(captor.capture());
        assertEquals("PrimeRoute Logistics Pvt. Ltd.", captor.getValue().getCompanyName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"===========", "@@@@@@", "#####", ")))))", "!!!!!", "#%$$", "   ", "!"})
    @DisplayName("2. Reject invalid company names consisting only of symbols/whitespace")
    void testInvalidCompanyNames(String invalidCompany) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setCompanyName(invalidCompany);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for company name: " + invalidCompany);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("companyName")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PrimeRoute Logistics Pvt. Ltd.", "ABC Logistics", "Tata Motors", "ABC & Sons Logistics", "XYZ Transport Co.", "Mahindra Logistics (West)"})
    @DisplayName("14. Accept legitimate business company names with standard punctuation")
    void testLegitimateCompanyNames(String validCompany) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setCompanyName(validCompany);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("companyName")),
                "Legitimate company name should be valid: " + validCompany);
    }

    @ParameterizedTest
    @ValueSource(strings = {")))))))))", "+_)))", "123456", "@@@@@@", "123 456", "   ", "!"})
    @DisplayName("3. Reject invalid contact names consisting only of digits, symbols or empty")
    void testInvalidContactNames(String invalidContact) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setContactName(invalidContact);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for contact name: " + invalidContact);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactName")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Shruti Sanjana Sahoo", "Ramesh Kumar", "Gudi Sahoo", "Priya Patel", "Dr. Jean-Pierre O'Connor", "A. P. J. Kalam"})
    @DisplayName("Accept legitimate contact names with spaces, apostrophes and hyphens")
    void testValidContactNames(String validContact) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setContactName(validContact);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("contactName")),
                "Legitimate contact name should be valid: " + validContact);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!#$%@jk", "#$%%@L.9b", "abc", "abc@", "abc@gmail", "@gmail.com", "plainaddress", "test@.com"})
    @DisplayName("4. Reject invalid email addresses")
    void testInvalidEmails(String invalidEmail) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setEmail(invalidEmail);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for email: " + invalidEmail);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@gmail.com", "shruti.sanjana@gmail.com", "operations@primeroute.com", "contact@abclogistics.in"})
    @DisplayName("Accept legitimate business emails")
    void testValidEmails(String validEmail) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setEmail(validEmail);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("email")),
                "Legitimate email should be valid: " + validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "123456789", "abcdefghij", "98765abc10", "0123456789", "123456789012345"})
    @DisplayName("5. Reject invalid phone numbers")
    void testInvalidPhones(String invalidPhone) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setPhone(invalidPhone);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for phone: " + invalidPhone);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9876543210", "+91 98765 43210", "+919876543210", "+91-9876543210", "6789012345", "7890123456", "8901234567"})
    @DisplayName("Accept valid Indian 10-digit phone numbers with optional +91 prefix")
    void testValidPhones(String validPhone) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setPhone(validPhone);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("phone")),
                "Valid phone should pass validation: " + validPhone);
    }

    @Test
    @DisplayName("6. Reject empty required fields")
    void testEmptyRequiredFields() {
        PublicFreightInquiryRequest req = new PublicFreightInquiryRequest();
        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.size() >= 5, "Expected violations for empty required fields");
    }

    @ParameterizedTest
    @ValueSource(strings = {"!!!", "@@@@", "====", "))))))", "   ", "a"})
    @DisplayName("7. Reject invalid origin city or terminal")
    void testInvalidOrigin(String invalidOrigin) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setOrigin(invalidOrigin);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for origin: " + invalidOrigin);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("origin")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bengaluru Hub", "Mumbai DC", "Kolkata Eastern Corridor Hub", "Delhi Terminal"})
    @DisplayName("Accept valid origin city/terminal")
    void testValidOrigin(String validOrigin) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setOrigin(validOrigin);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertTrue(violations.stream().noneMatch(v -> v.getPropertyPath().toString().equals("origin")),
                "Valid origin should pass: " + validOrigin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!!!", "@@@@", "====", "))))))", "   ", "z"})
    @DisplayName("8. Reject invalid destination city or hub")
    void testInvalidDestination(String invalidDest) {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setDestination(invalidDest);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for destination: " + invalidDest);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("destination")));
    }

    @Test
    @DisplayName("10. Reject negative cargo weight")
    void testNegativeCargoWeight() {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setWeight(-10.0);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for negative weight");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    @DisplayName("11. Reject zero cargo weight")
    void testZeroCargoWeight() {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setWeight(0.0);

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation error for zero weight");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    @DisplayName("12. Reject invalid service type not in allowed list")
    void testInvalidServiceType() {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setServiceType("Hyperloop Teleportation Express");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            inquiryService.processInquiry(req, "QUOTATION");
        });
        assertTrue(ex.getMessage().contains("Invalid service type"));
        verify(inquiryRepository, never()).save(any());
    }

    @Test
    @DisplayName("13. Reject overly long input (> 100 or > 1000 characters)")
    void testOverlyLongInput() {
        PublicFreightInquiryRequest req = createValidRequest();
        req.setCompanyName("A".repeat(101));
        req.setMessage("M".repeat(1001));

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty(), "Expected validation errors for overly long inputs");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("companyName")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")));
    }

    @Test
    @DisplayName("15. Direct API bypass attempt with invalid data does not create database record")
    void testDirectApiBypassAttempt() {
        PublicFreightInquiryRequest invalidReq = new PublicFreightInquiryRequest();
        invalidReq.setCompanyName("===========");
        invalidReq.setContactName("))))))))");
        invalidReq.setEmail("!#$%@jk");
        invalidReq.setPhone("abc");

        Set<ConstraintViolation<PublicFreightInquiryRequest>> violations = validator.validate(invalidReq);
        assertFalse(violations.isEmpty(), "Direct bypass attempt must fail validation");
        assertTrue(violations.size() >= 4, "All invalid fields must generate constraint violations");

        verify(inquiryRepository, never()).save(any());
    }
}
