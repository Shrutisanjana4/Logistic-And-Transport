package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.driver.CompleteDeliveryRequest;
import com.primeroute.logistics.dto.driver.DriverDeliveryDTO;
import com.primeroute.logistics.dto.driver.DriverLocationUpdateRequest;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DriverLiveLocationGpsTelemetryIntegrationTest {

    @Autowired
    private DriverPortalService driverPortalService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    private Driver driverA;
    private Driver driverB;
    private Shipment shipmentA;
    private Shipment shipmentB;

    @BeforeEach
    void setUp() {
        // Create or get warehouse
        Warehouse warehouse = warehouseRepository.findAll().stream().findFirst().orElseGet(() -> {
            Warehouse wh = new Warehouse();
            wh.setWarehouseCode("WH-GPS-001");
            wh.setWarehouseName("Bangalore Telemetry Hub");
            wh.setAddressLine1("Bommasandra Industrial Area");
            wh.setCity("Bengaluru");
            wh.setState("Karnataka");
            wh.setPostalCode("560099");
            wh.setCountry("India");
            wh.setLatitude(12.9716);
            wh.setLongitude(77.5946);
            wh.setStatus(WarehouseStatus.ACTIVE);
            wh.setCreatedAt(LocalDateTime.now());
            wh.setUpdatedAt(LocalDateTime.now());
            return warehouseRepository.save(wh);
        });

        // User & Driver A
        User userA = userRepository.findByEmailIgnoreCase("gps.driver.a@primeroute.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("gps.driver.a@primeroute.com");
            u.setPassword("$2a$10$xyz");
            u.setFullName("Ramesh Kumar");
            u.setRole(Role.DRIVER);
            u.setStatus("ACTIVE");
            u.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u);
        });

        driverA = driverRepository.findByEmailIgnoreCase("gps.driver.a@primeroute.com").orElseGet(() -> {
            Driver d = new Driver();
            d.setEmail("gps.driver.a@primeroute.com");
            d.setFullName("Ramesh Kumar");
            d.setPhone("+91 9876543210");
            d.setLicenseNumber("DL-GPS-KA01-001");
            d.setLicenseExpiryDate(LocalDate.now().plusYears(3));
            d.setStatus(DriverStatus.ON_TRIP);
            d.setWarehouse(warehouse);
            d.setCreatedAt(LocalDateTime.now());
            d.setUpdatedAt(LocalDateTime.now());
            return driverRepository.save(d);
        });
        driverA.setStatus(DriverStatus.ON_TRIP);
        driverRepository.save(driverA);

        // User & Driver B
        User userB = userRepository.findByEmailIgnoreCase("gps.driver.b@primeroute.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("gps.driver.b@primeroute.com");
            u.setPassword("$2a$10$xyz");
            u.setFullName("Suresh Patel");
            u.setRole(Role.DRIVER);
            u.setStatus("ACTIVE");
            u.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u);
        });

        driverB = driverRepository.findByEmailIgnoreCase("gps.driver.b@primeroute.com").orElseGet(() -> {
            Driver d = new Driver();
            d.setEmail("gps.driver.b@primeroute.com");
            d.setFullName("Suresh Patel");
            d.setPhone("+91 9876543211");
            d.setLicenseNumber("DL-GPS-KA01-002");
            d.setLicenseExpiryDate(LocalDate.now().plusYears(2));
            d.setStatus(DriverStatus.ON_TRIP);
            d.setWarehouse(warehouse);
            d.setCreatedAt(LocalDateTime.now());
            d.setUpdatedAt(LocalDateTime.now());
            return driverRepository.save(d);
        });

        // Customer
        User customer = userRepository.findByEmailIgnoreCase("gps.customer@test.com").orElseGet(() -> {
            User u = new User();
            u.setEmail("gps.customer@test.com");
            u.setPassword("$2a$10$xyz");
            u.setFullName("Alpha Customer");
            u.setRole(Role.CUSTOMER);
            u.setStatus("ACTIVE");
            u.setCreatedAt(LocalDateTime.now());
            return userRepository.save(u);
        });

        Address pickupAddr = new Address();
        pickupAddr.setCustomer(customer);
        pickupAddr.setAddressType("WAREHOUSE");
        pickupAddr.setContactName("Warehouse Bangalore Dispatch");
        pickupAddr.setPhone("+91 9988776655");
        pickupAddr.setAddressLine1("Bommasandra Industrial Area");
        pickupAddr.setCity("Bengaluru");
        pickupAddr.setState("Karnataka");
        pickupAddr.setPostalCode("560099");
        pickupAddr.setCountry("India");
        pickupAddr.setLatitude(12.9716);
        pickupAddr.setLongitude(77.5946);
        pickupAddr = addressRepository.save(pickupAddr);

        Address deliveryAddr = new Address();
        deliveryAddr.setCustomer(customer);
        deliveryAddr.setAddressType("DESTINATION");
        deliveryAddr.setContactName("Chennai Consignee Depot");
        deliveryAddr.setPhone("+91 9988776656");
        deliveryAddr.setAddressLine1("Mount Road commercial terminal");
        deliveryAddr.setCity("Chennai");
        deliveryAddr.setState("Tamil Nadu");
        deliveryAddr.setPostalCode("600002");
        deliveryAddr.setCountry("India");
        deliveryAddr.setLatitude(13.0827);
        deliveryAddr.setLongitude(80.2707);
        deliveryAddr = addressRepository.save(deliveryAddr);

        // Order A & Shipment A (Assigned to Driver A)
        Order orderA = new Order();
        orderA.setOrderNumber("PR-ORD-GPS-001");
        orderA.setCustomer(customer);
        orderA.setPickupAddress(pickupAddr);
        orderA.setDeliveryAddress(deliveryAddr);
        orderA.setStatus(OrderStatus.IN_TRANSIT);
        orderA.setDeliveryType(DeliveryType.STANDARD);
        orderA.setPackageDescription("Precision Logistics Instruments");
        orderA.setPackageQuantity(2);
        orderA.setPackageWeight(14.5);
        orderA.setTotalAmount(2500.0);
        orderA.setCreatedAt(LocalDateTime.now());
        orderA.setUpdatedAt(LocalDateTime.now());
        orderA = orderRepository.save(orderA);

        shipmentA = new Shipment();
        shipmentA.setTrackingNumber("PR-TRK-GPS-AAA1");
        shipmentA.setOrder(orderA);
        shipmentA.setDriver(driverA);
        shipmentA.setOriginWarehouse(warehouse);
        shipmentA.setStatus(OrderStatus.IN_TRANSIT);
        shipmentA.setOperationalStage("IN_TRANSIT");
        shipmentA.setCurrentLatitude(12.9716);
        shipmentA.setCurrentLongitude(77.5946);
        shipmentA.setCurrentLocationName("Bengaluru Dispatch Center");
        shipmentA.setLastLocationUpdatedAt(LocalDateTime.now().minusMinutes(10));
        shipmentA.setCreatedAt(LocalDateTime.now());
        shipmentA.setUpdatedAt(LocalDateTime.now());
        shipmentA = shipmentRepository.save(shipmentA);

        // Order B & Shipment B (Assigned to Driver B)
        Order orderB = new Order();
        orderB.setOrderNumber("PR-ORD-GPS-002");
        orderB.setCustomer(customer);
        orderB.setPickupAddress(pickupAddr);
        orderB.setDeliveryAddress(deliveryAddr);
        orderB.setStatus(OrderStatus.IN_TRANSIT);
        orderB.setDeliveryType(DeliveryType.EXPRESS);
        orderB.setPackageDescription("Pharmaceutical Cold Packs");
        orderB.setPackageQuantity(1);
        orderB.setPackageWeight(5.0);
        orderB.setTotalAmount(1800.0);
        orderB.setCreatedAt(LocalDateTime.now());
        orderB.setUpdatedAt(LocalDateTime.now());
        orderB = orderRepository.save(orderB);

        shipmentB = new Shipment();
        shipmentB.setTrackingNumber("PR-TRK-GPS-BBB2");
        shipmentB.setOrder(orderB);
        shipmentB.setDriver(driverB);
        shipmentB.setOriginWarehouse(warehouse);
        shipmentB.setStatus(OrderStatus.IN_TRANSIT);
        shipmentB.setOperationalStage("IN_TRANSIT");
        shipmentB.setCurrentLatitude(13.0000);
        shipmentB.setCurrentLongitude(77.7000);
        shipmentB.setCurrentLocationName("Hoskote Toll Plaza");
        shipmentB.setLastLocationUpdatedAt(LocalDateTime.now().minusMinutes(2));
        shipmentB.setCreatedAt(LocalDateTime.now());
        shipmentB.setUpdatedAt(LocalDateTime.now());
        shipmentB = shipmentRepository.save(shipmentB);
    }

    private void authenticateAs(String email, Role role) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                email,
                "n/a",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("1. Own Driver GPS update transmits coordinates and persists to Shipment in PostgreSQL")
    void testOwnDriverGpsUpdatePersistsSuccessfully() {
        authenticateAs(driverA.getEmail(), Role.DRIVER);

        DriverLocationUpdateRequest req = new DriverLocationUpdateRequest();
        req.setShipmentId(shipmentA.getId());
        req.setLatitude(12.9850);
        req.setLongitude(77.6320);
        req.setLocationName("Indiranagar 100ft Road Corridor");
        req.setAccuracy(8.5);
        req.setSpeed(42.0);
        req.setHeading(85.0);

        DriverDeliveryDTO result = driverPortalService.updateDriverLocation(null, req);

        assertNotNull(result);
        assertEquals("PR-TRK-GPS-AAA1", result.getTrackingNumber());

        // Verify PostgreSQL entity persistence
        Shipment updated = shipmentRepository.findById(shipmentA.getId()).orElseThrow();
        assertEquals(12.9850, updated.getCurrentLatitude(), 0.0001);
        assertEquals(77.6320, updated.getCurrentLongitude(), 0.0001);
        assertEquals("Indiranagar 100ft Road Corridor", updated.getCurrentLocationName());
        assertNotNull(updated.getLastLocationUpdatedAt());
        assertTrue(updated.getLastLocationUpdatedAt().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    @DisplayName("2. Driver attempting to update another driver's shipment GPS is rejected with 403 AccessDeniedException")
    void testDriverUpdateOtherDriverShipmentForbidden() {
        authenticateAs(driverA.getEmail(), Role.DRIVER);

        DriverLocationUpdateRequest req = new DriverLocationUpdateRequest();
        req.setShipmentId(shipmentB.getId()); // Belongs to Driver B
        req.setLatitude(13.0100);
        req.setLongitude(77.7200);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            driverPortalService.updateDriverLocation(null, req);
        });
        assertTrue(ex.getMessage().contains("Access Denied"));
    }

    @Test
    @DisplayName("3. Unauthenticated GPS update throws exception when resolving principal")
    void testUnauthenticatedGpsUpdateFails() {
        SecurityContextHolder.clearContext();

        DriverLocationUpdateRequest req = new DriverLocationUpdateRequest();
        req.setShipmentId(shipmentA.getId());
        req.setLatitude(12.9850);
        req.setLongitude(77.6320);

        assertThrows(Exception.class, () -> {
            driverPortalService.updateDriverLocation(null, req);
        });
    }

    @Test
    @DisplayName("4. Updating GPS for a DELIVERED shipment is prohibited and throws IllegalStateException")
    void testGpsUpdateForDeliveredShipmentFails() {
        authenticateAs(driverA.getEmail(), Role.DRIVER);

        // Mark shipment as DELIVERED
        shipmentA.setStatus(OrderStatus.DELIVERED);
        shipmentA.setOperationalStage("DELIVERED");
        shipmentRepository.saveAndFlush(shipmentA);

        DriverLocationUpdateRequest req = new DriverLocationUpdateRequest();
        req.setShipmentId(shipmentA.getId());
        req.setLatitude(13.0827);
        req.setLongitude(80.2707);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            driverPortalService.updateDriverLocation(null, req);
        });
        assertTrue(ex.getMessage().contains("GPS updates are permitted only for active deliveries"));
    }

    @Test
    @DisplayName("5. Delivery completion with ePOD stops active GPS broadcasting and restores driver to AVAILABLE")
    void testDeliveryCompletionRestoresDriverStatusAndPreservesFinalLocation() {
        authenticateAs(driverA.getEmail(), Role.DRIVER);

        // Step 1: Record in-flight GPS telemetry
        DriverLocationUpdateRequest locReq = new DriverLocationUpdateRequest();
        locReq.setShipmentId(shipmentA.getId());
        locReq.setLatitude(13.0810);
        locReq.setLongitude(80.2690);
        locReq.setLocationName("Chennai Port Outer Gate");
        driverPortalService.updateDriverLocation(null, locReq);

        // Step 2: Mark OUT_FOR_DELIVERY
        driverPortalService.markOutForDelivery(shipmentA.getId());

        // Step 3: Complete Delivery with electronic Proof of Delivery
        CompleteDeliveryRequest podReq = new CompleteDeliveryRequest();
        podReq.setRecipientName("R. Sundaram");
        podReq.setReceivedBy("R. Sundaram");
        podReq.setSignatureData("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
        podReq.setDeliveryNotes("Delivered in pristine condition with consignee seal verified.");

        DriverDeliveryDTO completed = driverPortalService.completeDelivery(shipmentA.getId(), podReq);
        assertNotNull(completed);
        assertEquals("DELIVERED", completed.getStatus());

        // Step 4: Verify driver status in database is restored to AVAILABLE
        Driver refreshedDriver = driverRepository.findById(driverA.getId()).orElseThrow();
        assertEquals(DriverStatus.AVAILABLE, refreshedDriver.getStatus(), "Driver status should be AVAILABLE after trip completion");

        // Step 5: Verify final recorded location is preserved on shipment
        Shipment deliveredShipment = shipmentRepository.findById(shipmentA.getId()).orElseThrow();
        assertEquals(OrderStatus.DELIVERED, deliveredShipment.getStatus());
        assertEquals(13.0810, deliveredShipment.getCurrentLatitude(), 0.0001);
        assertEquals(80.2690, deliveredShipment.getCurrentLongitude(), 0.0001);
        assertEquals("Chennai Port Outer Gate", deliveredShipment.getCurrentLocationName());
        assertNotNull(deliveredShipment.getLastLocationUpdatedAt());
    }

    @Test
    @DisplayName("6. Cross-portal GPS synchronization: Driver GPS update is immediately reflected across Warehouse and Customer views")
    void testCrossPortalGpsSynchronization() {
        authenticateAs(driverA.getEmail(), Role.DRIVER);

        // Driver transmits real-time coordinates
        double liveLat = 12.9249;
        double liveLon = 77.6784;
        String liveLocName = "Bellandur Outer Ring Road Express Hub";

        DriverLocationUpdateRequest req = new DriverLocationUpdateRequest();
        req.setShipmentId(shipmentA.getId());
        req.setLatitude(liveLat);
        req.setLongitude(liveLon);
        req.setLocationName(liveLocName);

        driverPortalService.updateDriverLocation(null, req);

        // Verify that shipment in database has exact same coordinates for Warehouse & Customer portals
        Shipment sharedShipment = shipmentRepository.findByTrackingNumber("PR-TRK-GPS-AAA1").orElseThrow();
        assertEquals(liveLat, sharedShipment.getCurrentLatitude(), 0.0001);
        assertEquals(liveLon, sharedShipment.getCurrentLongitude(), 0.0001);
        assertEquals(liveLocName, sharedShipment.getCurrentLocationName());
    }
}
