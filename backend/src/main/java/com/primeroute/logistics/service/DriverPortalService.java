package com.primeroute.logistics.service;

import com.primeroute.logistics.exception.ResourceNotFoundException;

import java.security.Principal;

import com.primeroute.logistics.dto.driver.*;
import com.primeroute.logistics.dto.driver.DriverLocationUpdateRequest;
import java.time.format.DateTimeFormatter;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DriverPortalService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShipmentTrackingEventRepository shipmentTrackingEventRepository;

    @Autowired
    private ProofOfDeliveryRepository proofOfDeliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoadRoutingService roadRoutingService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceService invoiceService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Resolves the authenticated driver from the current SecurityContext.
     */
    public Driver getAuthenticatedDriver() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        String principal = auth.getName();

        // 1. Try finding driver by email
        Optional<Driver> driverOpt = driverRepository.findByEmailIgnoreCase(principal);
        if (driverOpt.isPresent()) {
            return driverOpt.get();
        }

        // 2. Try finding user and matching driver by user email/id
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(principal);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<Driver> dByUser = driverRepository.findByEmailIgnoreCase(user.getEmail());
            if (dByUser.isPresent()) {
                return dByUser.get();
            }
        }



        throw new SecurityException("No driver profile linked to account: " + principal);
    }

    /**
     * Retrieves the profile of the authenticated driver.
     */
    @Transactional(readOnly = true)
    public DriverProfileDTO getDriverProfile() {
        Driver driver = getAuthenticatedDriver();
        DriverProfileDTO dto = new DriverProfileDTO();
        dto.setId(driver.getId());
        dto.setDriverId("DRV-" + String.format("%03d", driver.getId()));
        dto.setDriverCode("DRV-" + String.format("%03d", driver.getId()));
        dto.setFullName(driver.getFullName());
        dto.setName(driver.getFullName());
        dto.setEmail(driver.getEmail());
        dto.setPhone(driver.getPhone());
        dto.setLicenseNumber(driver.getLicenseNumber());
        if (driver.getLicenseExpiryDate() != null) {
            dto.setLicenseExpiry(driver.getLicenseExpiryDate().format(DATE_FMT));
        }
        dto.setStatus(driver.getStatus() != null ? driver.getStatus().name() : "AVAILABLE");
        dto.setAddress(driver.getAddress());

        if (driver.getWarehouse() != null) {
            dto.setAssignedWarehouseId(driver.getWarehouse().getId());
            dto.setAssignedWarehouseCode(driver.getWarehouse().getWarehouseCode());
            dto.setAssignedWarehouseName(driver.getWarehouse().getWarehouseName());
            dto.setAssignedHub(driver.getWarehouse().getWarehouseName() + " (" + driver.getWarehouse().getCity() + ")");
        }

        // Check assigned vehicle
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle v : vehicles) {
            if (v.getAssignedDriver() != null && v.getAssignedDriver().getId().equals(driver.getId())) {
                dto.setAssignedVehicleId(v.getId());
                dto.setAssignedVehicleCode(v.getVehicleNumber());
                dto.setAssignedVehiclePlate(v.getRegistrationNumber() != null ? v.getRegistrationNumber() : v.getVehicleNumber());
                dto.setAssignedVehicleModel(v.getModel() != null ? v.getModel() : v.getVehicleType().name());
                dto.setAssignedVehicleType(v.getVehicleType() != null ? v.getVehicleType().name() : "TRUCK");
                break;
            }
        }

        // Deliveries stats
        List<Shipment> delivered = shipmentRepository.findByDriverAndStatus(driver, OrderStatus.DELIVERED);
        dto.setTotalDeliveriesCompleted(delivered != null ? delivered.size() : 0);
        dto.setTotalTripsCompleted(delivered != null ? delivered.size() : 0);
        dto.setOnTimeRatePercentage(100.0);
        dto.setSafetyRating(5.0);
        if (driver.getCreatedAt() != null) {
            dto.setShiftStartedAt(driver.getCreatedAt().format(DATE_FMT));
        }

        return dto;
    }

    /**
     * Retrieves the vehicle currently assigned to the authenticated driver.
     */
    @Transactional(readOnly = true)
    public DriverVehicleDTO getAssignedVehicle() {
        Driver driver = getAuthenticatedDriver();
        List<Vehicle> vehicles = vehicleRepository.findAll();
        for (Vehicle v : vehicles) {
            if (v.getAssignedDriver() != null && v.getAssignedDriver().getId().equals(driver.getId())) {
                DriverVehicleDTO dto = new DriverVehicleDTO();
                dto.setId(v.getId());
                dto.setVehicleNumber(v.getVehicleNumber());
                dto.setVehicleCode(v.getVehicleNumber());
                dto.setPlateNumber(v.getRegistrationNumber() != null ? v.getRegistrationNumber() : v.getVehicleNumber());
                dto.setLicensePlate(v.getRegistrationNumber() != null ? v.getRegistrationNumber() : v.getVehicleNumber());
                dto.setRegistrationNumber(v.getRegistrationNumber());
                dto.setManufacturer(v.getManufacturer());
                dto.setModel(v.getModel());
                dto.setType(v.getVehicleType() != null ? v.getVehicleType().name() : "CONTAINER_TRUCK");
                dto.setVehicleType(v.getVehicleType() != null ? v.getVehicleType().name() : "CONTAINER_TRUCK");
                dto.setCapacity(v.getCapacity() != null ? v.getCapacity() : 10.0);
                dto.setCapacityTons(v.getCapacity() != null ? v.getCapacity() : 10.0);
                dto.setCapacityUnit(v.getCapacityUnit() != null ? v.getCapacityUnit() : "TONS");
                dto.setStatus(v.getStatus() != null ? v.getStatus().name() : "ASSIGNED");
                dto.setCurrentLocation(v.getCurrentLocation() != null ? v.getCurrentLocation() : (v.getWarehouse() != null ? v.getWarehouse().getWarehouseName() : "In Transit"));
                dto.setFuelLevelPercent(78);
                dto.setEngineTempCelsius(88);
                dto.setTirePressurePsi(35);
                dto.setOdometerKm(50600);
                dto.setLastInspectionDate(LocalDateTime.now().format(DATE_FMT));
                if (v.getInsuranceExpiry() != null) dto.setInsuranceExpiry(v.getInsuranceExpiry().format(DATE_FMT));
                if (v.getRegistrationExpiry() != null) dto.setRegistrationExpiry(v.getRegistrationExpiry().format(DATE_FMT));
                dto.setPucExpiry(v.getInsuranceExpiry() != null ? v.getInsuranceExpiry().format(DATE_FMT) : "31 Dec 2026");

                if (v.getWarehouse() != null) {
                    dto.setAssignedWarehouseId(v.getWarehouse().getId());
                    dto.setAssignedWarehouseCode(v.getWarehouse().getWarehouseCode());
                    dto.setAssignedWarehouseName(v.getWarehouse().getWarehouseName());
                }
                return dto;
            }
        }
        return null;
    }

    /**
     * Retrieves dashboard KPIs for the driver.
     */
    @Transactional(readOnly = true)
    public DriverDashboardDTO getDriverDashboard() {
        Driver driver = getAuthenticatedDriver();
        DriverDashboardDTO dashboard = new DriverDashboardDTO();
        dashboard.setDriverId("DRV-" + String.format("%03d", driver.getId()));
        dashboard.setDriverCode("DRV-" + String.format("%03d", driver.getId()));
        dashboard.setDriverName(driver.getFullName());
        dashboard.setDriverStatus(driver.getStatus() != null ? driver.getStatus().name() : "AVAILABLE");

        // Profile & Vehicle
        dashboard.setProfile(getDriverProfile());
        dashboard.setAssignedVehicle(getAssignedVehicle());

        // Active Delivery
        DriverDeliveryDTO active = getActiveDelivery();
        dashboard.setActiveDelivery(active);

        // Deliveries counts
        List<Shipment> allShipments = shipmentRepository.findByDriver(driver);
        int total = 0;
        int completed = 0;
        int pending = 0;
        List<DriverDeliveryDTO> recent = new ArrayList<>();

        if (allShipments != null) {
            for (Shipment s : allShipments) {
                total++;
                if (s.getStatus() == OrderStatus.DELIVERED) {
                    completed++;
                } else if (s.getStatus() != OrderStatus.CANCELLED) {
                    pending++;
                }
                if (recent.size() < 5) {
                    recent.add(toDriverDeliveryDTO(s));
                }
            }
        }

        dashboard.setTodaysDeliveries(total);
        dashboard.setAssignedToday(total);
        dashboard.setCompletedDeliveries(completed);
        dashboard.setCompletedToday(completed);
        dashboard.setPendingDeliveries(pending);
        dashboard.setPendingToday(pending);
        dashboard.setOnTimeDeliveries(100.0);
        dashboard.setRecentDeliveries(recent);

        return dashboard;
    }

    /**
     * Lists all deliveries assigned to the driver, optionally filtered by status.
     */
    @Transactional(readOnly = true)
    public List<DriverDeliveryDTO> getDeliveries(String statusFilter) {
        Driver driver = getAuthenticatedDriver();
        List<Shipment> shipments;

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            try {
                OrderStatus os = OrderStatus.valueOf(statusFilter.toUpperCase());
                shipments = shipmentRepository.findByDriverAndStatus(driver, os);
            } catch (IllegalArgumentException e) {
                shipments = shipmentRepository.findByDriver(driver);
            }
        } else {
            shipments = shipmentRepository.findByDriver(driver);
        }

        List<DriverDeliveryDTO> result = new ArrayList<>();
        if (shipments != null) {
            for (Shipment s : shipments) {
                result.add(toDriverDeliveryDTO(s));
            }
        }
        return result;
    }

    /**
     * Returns the currently active delivery (IN_TRANSIT or OUT_FOR_DELIVERY) for the driver.
     */
    @Transactional(readOnly = true)
    public DriverDeliveryDTO getActiveDelivery() {
        Driver driver = getAuthenticatedDriver();
        List<Shipment> activeShipments = shipmentRepository.findByDriver(driver);
        if (activeShipments == null || activeShipments.isEmpty()) {
            return null;
        }

        // 1. In-flight (highest priority)
        for (Shipment s : activeShipments) {
            if (s.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                return toDriverDeliveryDTO(s);
            }
        }
        for (Shipment s : activeShipments) {
            if (s.getStatus() == OrderStatus.IN_TRANSIT) {
                return toDriverDeliveryDTO(s);
            }
        }
        for (Shipment s : activeShipments) {
            if (s.getStatus() == OrderStatus.PICKED_UP) {
                return toDriverDeliveryDTO(s);
            }
        }

        // 2. Ready / Assigned to driver
        for (Shipment s : activeShipments) {
            if (s.getStatus() == OrderStatus.CONFIRMED || s.getStatus() == OrderStatus.PENDING) {
                return toDriverDeliveryDTO(s);
            }
        }

        // 3. Fallback to any active non-completed shipment
        for (Shipment s : activeShipments) {
            if (s.getStatus() != OrderStatus.DELIVERED && s.getStatus() != OrderStatus.CANCELLED) {
                return toDriverDeliveryDTO(s);
            }
        }

        return null;
    }

    /**
     * Fetches delivery details by shipment ID, enforcing strict driver ownership.
     */
    @Transactional(readOnly = true)
    public DriverDeliveryDTO getDeliveryById(Long shipmentId) {
        Driver driver = getAuthenticatedDriver();
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driver.getId())) {
            throw new SecurityException("Access Denied: You do not have permission to access shipment #" + shipmentId);
        }

        return toDriverDeliveryDTO(shipment);
    }

    /**
     * STEP 2: Transition shipment from IN_TRANSIT -> OUT_FOR_DELIVERY.
     */
    @Transactional
    public DriverDeliveryDTO markOutForDelivery(Long shipmentId) {
        Driver driver = getAuthenticatedDriver();
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driver.getId())) {
            throw new SecurityException("Access Denied: You do not have permission to operate shipment #" + shipmentId);
        }

        if (shipment.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            return toDriverDeliveryDTO(shipment);
        }

        if (shipment.getStatus() == OrderStatus.DELIVERED || shipment.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot mark a " + shipment.getStatus() + " shipment as OUT_FOR_DELIVERY.");
        }

        shipment.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        shipment.setOperationalStage("OUT_FOR_DELIVERY");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        Order order = shipment.getOrder();
        if (order != null) {
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setStatus("OUT_FOR_DELIVERY");
        event.setTitle("Out for Delivery");
        String destCity = (order != null && order.getDeliveryAddress() != null) ? order.getDeliveryAddress().getCity() : "Customer Destination";
        event.setLocationName(destCity);
        event.setDescription("Shipment is out for delivery with driver " + driver.getFullName());
        event.setEventTime(LocalDateTime.now());
        if (order != null && order.getDeliveryAddress() != null && order.getDeliveryAddress().getLatitude() != null) {
            event.setLatitude(order.getDeliveryAddress().getLatitude());
            event.setLongitude(order.getDeliveryAddress().getLongitude());
        }
        shipmentTrackingEventRepository.save(event);

        if (driver.getStatus() != DriverStatus.ON_TRIP) {
            driver.setStatus(DriverStatus.ON_TRIP);
            driver.setUpdatedAt(LocalDateTime.now());
            driverRepository.save(driver);
        }

        Vehicle vehicle = shipment.getVehicle();
        if (vehicle != null && vehicle.getStatus() != VehicleStatus.IN_TRANSIT) {
            vehicle.setStatus(VehicleStatus.IN_TRANSIT);
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }

        return toDriverDeliveryDTO(shipment);
    }

    /**
     * STEP 2: Start Trip / Start Delivery.
     */
    @Transactional
    public DriverDeliveryDTO startTrip(Long shipmentId) {
        Driver driver = getAuthenticatedDriver();
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driver.getId())) {
            throw new SecurityException("Access Denied: You do not have permission to operate shipment #" + shipmentId);
        }

        if (shipment.getStatus() == OrderStatus.IN_TRANSIT || shipment.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            return toDriverDeliveryDTO(shipment);
        }

        shipment.setStatus(OrderStatus.IN_TRANSIT);
        shipment.setOperationalStage("DISPATCHED");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        Order order = shipment.getOrder();
        if (order != null) {
            order.setStatus(OrderStatus.IN_TRANSIT);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setStatus("IN_TRANSIT");
        event.setTitle("In Transit");
        event.setLocationName(shipment.getOriginWarehouse() != null ? shipment.getOriginWarehouse().getWarehouseName() : "Origin Warehouse");
        event.setDescription("Driver " + driver.getFullName() + " started transit towards customer destination.");
        event.setEventTime(LocalDateTime.now());
        shipmentTrackingEventRepository.save(event);

        driver.setStatus(DriverStatus.ON_TRIP);
        driver.setUpdatedAt(LocalDateTime.now());
        driverRepository.save(driver);

        Vehicle vehicle = shipment.getVehicle();
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.IN_TRANSIT);
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }

        return toDriverDeliveryDTO(shipment);
    }

    /**
     * STEP 3: Complete Delivery with Electronic Proof of Delivery (ePOD).
     */
    @Transactional
    public DriverDeliveryDTO completeDelivery(Long shipmentId, CompleteDeliveryRequest request) {
        Driver driver = getAuthenticatedDriver();
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        // 1. Verify Driver Ownership
        if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driver.getId())) {
            throw new SecurityException("Access Denied: You do not have permission to complete shipment #" + shipmentId);
        }

        // 2. Idempotency: If already DELIVERED, return current state
        if (shipment.getStatus() == OrderStatus.DELIVERED) {
            return toDriverDeliveryDTO(shipment);
        }

        // 3. Status Guard: Must be OUT_FOR_DELIVERY
        if (shipment.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Cannot complete delivery. Shipment must be in OUT_FOR_DELIVERY status (current status: " + shipment.getStatus() + ")");
        }

        // 4. Validate Signature
        String sig = request != null ? request.getSignatureData() : null;
        if (sig == null || sig.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer digital signature is required to complete delivery proof.");
        }

        Order order = shipment.getOrder();
        String recipientName = (request != null && request.getRecipientName() != null && !request.getRecipientName().trim().isEmpty())
                ? request.getRecipientName()
                : (order != null && order.getCustomer() != null ? order.getCustomer().getFullName() : "Customer");

        // 4b. Explicit Cash on Delivery (COD) Collection Guard & Verification
        boolean isCodOrder = order != null && order.getPaymentMethod() == PaymentMethod.COD;
        boolean isPaymentPending = order != null && order.getPaymentStatus() != PaymentStatus.PAID;

        if (isCodOrder && isPaymentPending) {
            boolean codConfirmed = request != null && Boolean.TRUE.equals(request.getCodCollected());
            if (!codConfirmed) {
                double due = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
                throw new IllegalArgumentException(
                        "Cash on Delivery payment of ₹" + String.format(java.util.Locale.US, "%.2f", due) +
                        " must be explicitly collected and confirmed by the driver before completing delivery.");
            }

            // Explicit cash collection verified -> Atomically update Order payment status to PAID
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            // Reconcile and synchronize linked Invoice to PAID status
            try {
                Invoice invoice = invoiceRepository.findByOrder(order)
                        .or(() -> invoiceRepository.findByOrderId(order.getId()))
                        .orElseGet(() -> invoiceService.getOrCreateInvoiceForOrder(order));

                if (invoice != null) {
                    invoice.setPaymentStatus(PaymentStatus.PAID);
                    invoice.setInvoiceStatus(InvoiceStatus.PAID);
                    invoice.setAmountPaid(invoice.getTotalAmount());
                    invoice.setBalanceDue(0.0);
                    invoice.setPaymentDate(java.time.LocalDate.now());
                    invoice.setPaymentMethod("COD");
                    invoice.setTransactionId("COD-COLL-DRV-" + driver.getId() + "-" + System.currentTimeMillis());
                    invoice.setNotes("Cash on Delivery collected in full (₹" + invoice.getTotalAmount() + ") by driver " + driver.getFullName() + " (DRV-" + driver.getId() + ")");
                    invoice.setUpdatedAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);
                }
            } catch (Exception ex) {
                // Log and proceed
            }
        }

        // 5. Persist Proof of Delivery (ePOD)
        ProofOfDelivery pod = proofOfDeliveryRepository.findByShipment(shipment).orElse(new ProofOfDelivery());
        pod.setShipment(shipment);
        pod.setRecipientName(recipientName);
        pod.setReceivedBy(request != null && request.getReceivedBy() != null ? request.getReceivedBy() : recipientName);
        pod.setSignatureData(sig);
        pod.setPhotoData(request != null ? request.getPhotoData() : null);
        String customNotes = request != null ? request.getDeliveryNotes() : null;
        if (isCodOrder) {
            String codNote = "Cash on Delivery collected (₹" + (order.getTotalAmount() != null ? order.getTotalAmount() : 0.0) + ")";
            pod.setDeliveryNotes(customNotes != null && !customNotes.isBlank() ? customNotes + " | " + codNote : codNote);
        } else {
            pod.setDeliveryNotes(customNotes != null && !customNotes.isBlank() ? customNotes : "Delivered directly to customer.");
        }
        pod.setDeliveredAt(LocalDateTime.now());
        pod.setDeliveredBy(driver.getFullName());
        if (request != null) {
            pod.setLatitude(request.getLatitude());
            pod.setLongitude(request.getLongitude());
        }
        if (pod.getCreatedAt() == null) {
            pod.setCreatedAt(LocalDateTime.now());
        }
        pod.setUpdatedAt(LocalDateTime.now());
        proofOfDeliveryRepository.save(pod);

        // 6. Update Shipment Status -> DELIVERED
        shipment.setStatus(OrderStatus.DELIVERED);
        shipment.setOperationalStage("DELIVERED");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        // 7. Update Order Status -> DELIVERED
        if (order != null) {
            order.setStatus(OrderStatus.DELIVERED);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        // 8. Create DELIVERED Tracking Event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setStatus("DELIVERED");
        event.setTitle("Delivered");
        String destCity = (order != null && order.getDeliveryAddress() != null) ? order.getDeliveryAddress().getCity() : "Customer Delivery Address";
        event.setLocationName(destCity);
        String eventDesc = "Shipment delivered successfully to " + recipientName + ". Electronic proof of delivery verified.";
        if (isCodOrder) {
            eventDesc += " Cash on Delivery payment received in full.";
        }
        event.setDescription(eventDesc);
        event.setEventTime(LocalDateTime.now());
        if (order != null && order.getDeliveryAddress() != null && order.getDeliveryAddress().getLatitude() != null) {
            event.setLatitude(order.getDeliveryAddress().getLatitude());
            event.setLongitude(order.getDeliveryAddress().getLongitude());
        }
        shipmentTrackingEventRepository.save(event);

        // 9. Release Driver Operational State -> AVAILABLE
        if (driver.getStatus() == DriverStatus.ON_TRIP) {
            driver.setStatus(DriverStatus.AVAILABLE);
            driver.setUpdatedAt(LocalDateTime.now());
            driverRepository.save(driver);
        }

        // 10. Release Vehicle Operational State -> ASSIGNED
        Vehicle vehicle = shipment.getVehicle();
        if (vehicle != null && vehicle.getStatus() == VehicleStatus.IN_TRANSIT) {
            vehicle.setStatus(VehicleStatus.ASSIGNED);
            vehicle.setUpdatedAt(LocalDateTime.now());
            vehicleRepository.save(vehicle);
        }

        return toDriverDeliveryDTO(shipment);
    }

    /**
     * Converts a Shipment entity into DriverDeliveryDTO.
     */
    
    /**
     * Updates real-time GPS telemetry for the authenticated driver's active shipment.
     * DRIVER-ONLY operation.
     */
    @Transactional
    public DriverDeliveryDTO updateDriverLocation(Principal principal, DriverLocationUpdateRequest request) {
        Driver driver = getAuthenticatedDriver();

        if (request == null) {
            throw new IllegalArgumentException("Location update request payload cannot be null");
        }

        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new IllegalArgumentException("Latitude and Longitude coordinates are required for GPS telemetry");
        }

        if (request.getLatitude() < -90.0 || request.getLatitude() > 90.0) {
            throw new IllegalArgumentException("Invalid latitude value: " + request.getLatitude() + ". Must be between -90.0 and 90.0");
        }

        if (request.getLongitude() < -180.0 || request.getLongitude() > 180.0) {
            throw new IllegalArgumentException("Invalid longitude value: " + request.getLongitude() + ". Must be between -180.0 and 180.0");
        }

        Shipment activeShipment = null;

        if (request.getShipmentId() != null) {
            Shipment target = shipmentRepository.findById(request.getShipmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + request.getShipmentId()));

            // Authorization check: Must be assigned to this driver
            if (target.getDriver() == null || !target.getDriver().getId().equals(driver.getId())) {
                throw new AccessDeniedException("Access Denied: You do not have permission to transmit telemetry for shipment #" + request.getShipmentId());
            }

            // Status check: Must be actively in-flight
            if (target.getStatus() != OrderStatus.IN_TRANSIT && target.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
                throw new IllegalStateException("Cannot update GPS telemetry for shipment in state " + target.getStatus() + ". GPS updates are permitted only for active deliveries (IN_TRANSIT or OUT_FOR_DELIVERY).");
            }

            activeShipment = target;
        } else {
            // Locate active shipment for driver
            List<Shipment> activeList = shipmentRepository.findActiveShipmentsByDriver(driver);
            Optional<Shipment> inFlight = activeList.stream()
                    .filter(s -> s.getStatus() == OrderStatus.IN_TRANSIT || s.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                    .findFirst();

            if (inFlight.isEmpty()) {
                throw new IllegalStateException("No active delivery (IN_TRANSIT or OUT_FOR_DELIVERY) found for driver " + driver.getFullName() + ". Start a delivery trip before broadcasting live GPS coordinates.");
            }
            activeShipment = inFlight.get();
        }

        // Store live position on Shipment
        activeShipment.setCurrentLatitude(request.getLatitude());
        activeShipment.setCurrentLongitude(request.getLongitude());
        if (request.getLocationName() != null && !request.getLocationName().isBlank()) {
            activeShipment.setCurrentLocationName(request.getLocationName().trim());
        }
        LocalDateTime now = LocalDateTime.now();
        activeShipment.setLastLocationUpdatedAt(now);
        activeShipment.setUpdatedAt(now);

        Shipment saved = shipmentRepository.save(activeShipment);
        return toDriverDeliveryDTO(saved);
    }

    public DriverDeliveryDTO toDriverDeliveryDTO(Shipment shipment) {
        DriverDeliveryDTO dto = new DriverDeliveryDTO();
        dto.setId(String.valueOf(shipment.getId()));
        dto.setShipmentId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());

        Order order = shipment.getOrder();
        if (order != null) {
            dto.setOrderNumber(order.getOrderNumber());
            dto.setCustomerOrderRef(order.getOrderNumber());
            dto.setTotalPackages(order.getPackageQuantity() != null ? order.getPackageQuantity() : 1);
            dto.setTotalQuantity(order.getPackageQuantity() != null ? order.getPackageQuantity() : 1);
            dto.setTotalWeightKg(order.getPackageWeight() != null ? order.getPackageWeight() : 5.0);
            dto.setVolumeCbm(0.45);
            dto.setCargoSummary(order.getPackageDescription() != null ? order.getPackageDescription() : "Consignment Cargo");

            Address pickupAddress = order.getPickupAddress();
            Address deliveryAddress = order.getDeliveryAddress();
            User customer = order.getCustomer();

            // Customer Destination
            DriverLocationDTO dest = new DriverLocationDTO();
            dest.setCustomerName(customer != null ? customer.getFullName() : (deliveryAddress != null ? deliveryAddress.getContactName() : "Customer"));
            dest.setCompanyName(customer != null ? customer.getCompanyName() : null);
            dest.setPhone(deliveryAddress != null && deliveryAddress.getPhone() != null ? deliveryAddress.getPhone() : (customer != null ? customer.getPhone() : "+91 98765 00000"));
            dest.setEmail(customer != null ? customer.getEmail() : "customer@client.com");
            dest.setAddress(deliveryAddress != null ? deliveryAddress.getAddressLine1() : "Customer Destination Address");
            dest.setCity(deliveryAddress != null ? deliveryAddress.getCity() : "Destination City");
            dest.setState(deliveryAddress != null ? deliveryAddress.getState() : "State");
            dest.setPostalCode(deliveryAddress != null ? deliveryAddress.getPostalCode() : "560001");
            dest.setDeliveryNotes("Customer delivery address");
            if (deliveryAddress != null && deliveryAddress.getLatitude() != null && deliveryAddress.getLongitude() != null) {
                dest.setLatitude(deliveryAddress.getLatitude());
                dest.setLongitude(deliveryAddress.getLongitude());
                dest.setCoordinates(new double[]{deliveryAddress.getLatitude(), deliveryAddress.getLongitude()});
            } else {
                dest.setLatitude(12.9716);
                dest.setLongitude(77.5946);
                dest.setCoordinates(new double[]{12.9716, 77.5946});
            }
            dto.setDeliveryLocation(dest);
            dto.setDestination(dest);
            dto.setRecipientName(dest.getCustomerName());
            dto.setRecipientPhone(dest.getPhone());
            dto.setRecipientEmail(dest.getEmail());
            dto.setRecipientCity(dest.getCity());

            // Origin Pickup Dock
            DriverLocationDTO orig = new DriverLocationDTO();
            Warehouse originWarehouse = shipment.getOriginWarehouse();
            if (originWarehouse != null) {
                orig.setFacilityName(originWarehouse.getWarehouseName());
                orig.setAddress(originWarehouse.getAddressLine1());
                orig.setCity(originWarehouse.getCity());
                orig.setState(originWarehouse.getState());
                orig.setPostalCode(originWarehouse.getPostalCode());
                orig.setDockBay("Bay-" + ((originWarehouse.getId() % 6) + 1));
                orig.setContactPerson(originWarehouse.getManagerName() != null ? originWarehouse.getManagerName() : "Warehouse Manager");
                orig.setContactPhone(originWarehouse.getContactPhone() != null ? originWarehouse.getContactPhone() : "+91 98001 11000");
                if (originWarehouse.getLatitude() != null && originWarehouse.getLongitude() != null) {
                    orig.setLatitude(originWarehouse.getLatitude());
                    orig.setLongitude(originWarehouse.getLongitude());
                    orig.setCoordinates(new double[]{originWarehouse.getLatitude(), originWarehouse.getLongitude()});
                }
            } else if (pickupAddress != null) {
                orig.setFacilityName(pickupAddress.getContactName() + " Facility");
                orig.setAddress(pickupAddress.getAddressLine1());
                orig.setCity(pickupAddress.getCity());
                orig.setState(pickupAddress.getState());
                orig.setPostalCode(pickupAddress.getPostalCode());
                orig.setDockBay("Bay-1");
                orig.setContactPerson(pickupAddress.getContactName());
                orig.setContactPhone(pickupAddress.getPhone());
            }
            dto.setPickupLocation(orig);

            // Cargo items
            List<DriverCargoItemDTO> cargoItems = new ArrayList<>();
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                for (OrderItem item : order.getItems()) {
                    DriverCargoItemDTO c = new DriverCargoItemDTO();
                    c.setId(String.valueOf(item.getId()));
                    c.setQuantity(item.getQuantity());
                    c.setWeightKg(item.getTotalWeight() != null ? item.getTotalWeight() : (item.getUnitWeight() != null ? item.getUnitWeight() * item.getQuantity() : 1.0));
                    if (item.getProduct() != null) {
                        c.setSku(item.getProduct().getSku());
                        c.setName(item.getProduct().getName());
                        c.setDescription(item.getProduct().getName());
                    } else {
                        c.setSku("PR-SKU-" + item.getId());
                        c.setName("Package Item");
                        c.setDescription(order.getPackageDescription());
                    }
                    c.setDimensions("Standard Box");
                    c.setHazardous(false);
                    cargoItems.add(c);
                }
            } else {
                DriverCargoItemDTO c = new DriverCargoItemDTO();
                c.setId("ITEM-1");
                c.setSku("SKU-PKG-01");
                c.setName("Consignment Package");
                c.setDescription(order.getPackageDescription() != null ? order.getPackageDescription() : "Commercial Goods Package");
                c.setQuantity(dto.getTotalPackages());
                c.setWeightKg(dto.getTotalWeightKg());
                c.setDimensions("Standard Master Carton");
                c.setHazardous(false);
                cargoItems.add(c);
            }
            dto.setCargoItems(cargoItems);

            // Financial & Cash on Delivery (COD) fields
            String pMethod = order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null;
            dto.setPaymentMethod(pMethod);
            dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "UNPAID");
            double totalAmt = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
            dto.setTotalAmount(totalAmt);
            dto.setCodAmount(totalAmt);
            boolean isCod = order.getPaymentMethod() == PaymentMethod.COD && order.getPaymentStatus() != PaymentStatus.PAID;
            dto.setIsCod(isCod);
            dto.setCodCollected(order.getPaymentStatus() == PaymentStatus.PAID);
        }

        // Status alignment
        dto.setStatus(shipment.getStatus() != null ? shipment.getStatus().name() : "IN_TRANSIT");
        dto.setOperationalStage(shipment.getOperationalStage() != null ? shipment.getOperationalStage() : "IN_TRANSIT");

        dto.setPriority(order != null && order.getDeliveryType() == DeliveryType.EXPRESS ? "CRITICAL" : "STANDARD");

        if (shipment.getAssignedAt() != null) {
            dto.setAssignedAt(shipment.getAssignedAt().format(DATE_FMT));
            dto.setAssignedDate(shipment.getAssignedAt().format(DATE_FMT));
        } else if (shipment.getCreatedAt() != null) {
            dto.setAssignedAt(shipment.getCreatedAt().format(DATE_FMT));
            dto.setAssignedDate(shipment.getCreatedAt().format(DATE_FMT));
        }

        if (shipment.getEstimatedDeliveryDate() != null) {
            dto.setEstimatedDeliveryTime(shipment.getEstimatedDeliveryDate().format(DATE_FMT));
            dto.setDeliveryEta(shipment.getEstimatedDeliveryDate().format(DATE_FMT) + " 05:00 PM");
        } else {
            dto.setEstimatedDeliveryTime("Today 05:00 PM");
            dto.setDeliveryEta("Today 05:00 PM");
        }
        dto.setPickupEta("09:00 AM");

        double dist = calculateShipmentDistanceKm(shipment);
        dto.setDistanceKm(Math.round(dist * 10.0) / 10.0);
        dto.setEstimatedDurationMins((int) (dist * 1.8));

        // Real Tracking Events
        List<ShipmentTrackingEvent> events = shipmentTrackingEventRepository.findByShipmentOrderByEventTimeAsc(shipment);
        List<DriverTrackingEventDTO> eventDTOs = new ArrayList<>();
        for (ShipmentTrackingEvent ev : events) {
            DriverTrackingEventDTO te = new DriverTrackingEventDTO();
            te.setId(ev.getId());
            te.setStatus(ev.getStatus());
            te.setTitle(ev.getTitle());
            te.setDescription(ev.getDescription());
            te.setLocation(ev.getLocationName());
            te.setTimestamp(ev.getEventTime() != null ? ev.getEventTime().format(TIME_FMT) : "");
            te.setLatitude(ev.getLatitude());
            te.setLongitude(ev.getLongitude());
            eventDTOs.add(te);
        }
        dto.setTrackingEvents(eventDTOs);

        // ePOD data if present
        Optional<ProofOfDelivery> podOpt = proofOfDeliveryRepository.findByShipment(shipment);
        if (podOpt.isPresent()) {
            ProofOfDelivery pod = podOpt.get();
            ProofOfDeliveryDTO podDto = new ProofOfDeliveryDTO();
            podDto.setId(pod.getId());
            podDto.setShipmentId(shipment.getId());
            podDto.setTrackingNumber(shipment.getTrackingNumber());
            podDto.setRecipientName(pod.getRecipientName());
            podDto.setReceivedBy(pod.getReceivedBy());
            podDto.setSignatureData(pod.getSignatureData());
            podDto.setSignatureBase64(pod.getSignatureData());
            podDto.setPhotoData(pod.getPhotoData());
            podDto.setPhotoUrl(pod.getPhotoData());
            podDto.setDeliveryNotes(pod.getDeliveryNotes());
            podDto.setNotes(pod.getDeliveryNotes());
            podDto.setDeliveredAt(pod.getDeliveredAt() != null ? pod.getDeliveredAt().format(TIME_FMT) : "");
            podDto.setDeliveredBy(pod.getDeliveredBy());
            podDto.setLatitude(pod.getLatitude());
            podDto.setLongitude(pod.getLongitude());
            dto.setProofOfDelivery(podDto);
        }

        // Live Telemetry GPS fields
        dto.setCurrentLatitude(shipment.getCurrentLatitude());
        dto.setCurrentLongitude(shipment.getCurrentLongitude());
        dto.setCurrentLocationName(shipment.getCurrentLocationName());
        if (shipment.getLastLocationUpdatedAt() != null) {
            dto.setLastLocationUpdatedAt(shipment.getLastLocationUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        return dto;
    }

    private double calculateShipmentDistanceKm(Shipment s) {
        if (s == null || s.getOrder() == null) return 24.5;
        Warehouse orig = s.getOriginWarehouse();
        Address da = s.getOrder().getDeliveryAddress();
        if (orig != null && da != null && orig.getLatitude() != null && orig.getLongitude() != null && da.getLatitude() != null && da.getLongitude() != null) {
            return haversineKm(orig.getLatitude(), orig.getLongitude(), da.getLatitude(), da.getLongitude());
        }
        return 28.4;
    }

    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c;
    }

    /**
     * Retrieves dynamic road route, geometry, distance, and ETA for the active delivery.
     */
    @Transactional(readOnly = true)
    public DriverRouteDTO getActiveDeliveryRoute() {
        Driver driver = getAuthenticatedDriver();
        List<Shipment> activeShipments = shipmentRepository.findByDriver(driver);
        if (activeShipments == null || activeShipments.isEmpty()) {
            return null;
        }

        Shipment active = null;
        for (Shipment s : activeShipments) {
            if (s.getStatus() == OrderStatus.OUT_FOR_DELIVERY) { active = s; break; }
        }
        if (active == null) {
            for (Shipment s : activeShipments) {
                if (s.getStatus() == OrderStatus.IN_TRANSIT) { active = s; break; }
            }
        }
        if (active == null) {
            for (Shipment s : activeShipments) {
                if (s.getStatus() == OrderStatus.PICKED_UP) { active = s; break; }
            }
        }
        if (active == null) {
            for (Shipment s : activeShipments) {
                if (s.getStatus() == OrderStatus.CONFIRMED || s.getStatus() == OrderStatus.PENDING) { active = s; break; }
            }
        }
        if (active == null) {
            for (Shipment s : activeShipments) {
                if (s.getStatus() != OrderStatus.DELIVERED && s.getStatus() != OrderStatus.CANCELLED) { active = s; break; }
            }
        }

        if (active == null) {
            return null;
        }
        return toDriverRouteDTO(active);
    }

    /**
     * Retrieves dynamic road route for a specific shipment ID with strict driver ownership validation.
     */
    @Transactional(readOnly = true)
    public DriverRouteDTO getDeliveryRoute(Long shipmentId) {
        if (shipmentId == null || shipmentId <= 0) {
            return getActiveDeliveryRoute();
        }
        Driver driver = getAuthenticatedDriver();
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found with ID: " + shipmentId));

        if (shipment.getDriver() == null || !shipment.getDriver().getId().equals(driver.getId())) {
            throw new SecurityException("Access Denied: You do not have permission to access shipment #" + shipmentId);
        }

        return toDriverRouteDTO(shipment);
    }

    /**
     * Maps shipment entity to dynamic road-routed DriverRouteDTO.
     */
    public DriverRouteDTO toDriverRouteDTO(Shipment shipment) {
        DriverRouteDTO dto = new DriverRouteDTO();
        dto.setShipmentId(shipment.getId());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        if (shipment.getOrder() != null) {
            dto.setOrderNumber(shipment.getOrder().getOrderNumber());
        }
        dto.setStatus(shipment.getStatus() != null ? shipment.getStatus().name() : "IN_TRANSIT");
        dto.setOperationalStage(shipment.getOperationalStage() != null ? shipment.getOperationalStage() : "IN_TRANSIT");

        // 1. Origin Warehouse
        Warehouse origWarehouse = shipment.getOriginWarehouse();
        DriverLocationDTO origLoc = new DriverLocationDTO();
        double origLat = 12.9716;
        double origLon = 77.5946;
        if (origWarehouse != null) {
            origLoc.setFacilityName(origWarehouse.getWarehouseName());
            origLoc.setAddress(origWarehouse.getAddressLine1());
            origLoc.setCity(origWarehouse.getCity());
            origLoc.setState(origWarehouse.getState());
            origLoc.setPostalCode(origWarehouse.getPostalCode());
            origLoc.setContactPhone(origWarehouse.getContactPhone() != null ? origWarehouse.getContactPhone() : origWarehouse.getManagerPhone());
            if (origWarehouse.getLatitude() != null && origWarehouse.getLongitude() != null) {
                origLat = origWarehouse.getLatitude();
                origLon = origWarehouse.getLongitude();
            }
        } else {
            origLoc.setFacilityName("Primary Dispatch Hub");
            origLoc.setAddress("Bommasandra Industrial Area");
            origLoc.setCity("Bengaluru");
            origLoc.setState("Karnataka");
            origLoc.setPostalCode("560099");
        }
        origLoc.setLatitude(origLat);
        origLoc.setLongitude(origLon);
        origLoc.setCoordinates(new double[]{origLat, origLon});
        dto.setOrigin(origLoc);

        // 2. Destination Address
        Order order = shipment.getOrder();
        Address da = order != null ? order.getDeliveryAddress() : null;
        DriverLocationDTO destLoc = new DriverLocationDTO();
        double destLat = 13.0827;
        double destLon = 80.2707;
        if (da != null) {
            destLoc.setCustomerName(da.getContactName());
            destLoc.setAddress(da.getAddressLine1() + (da.getAddressLine2() != null ? ", " + da.getAddressLine2() : ""));
            destLoc.setCity(da.getCity());
            destLoc.setState(da.getState());
            destLoc.setPostalCode(da.getPostalCode());
            destLoc.setPhone(da.getPhone());
            destLoc.setDeliveryNotes("Standard consignee receiving point");
            if (da.getLatitude() != null && da.getLongitude() != null) {
                destLat = da.getLatitude();
                destLon = da.getLongitude();
            } else if (geocodingService != null) {
                GeocodingService.Coordinates geocoded = geocodingService.geocode(da.getAddressLine1(), da.getCity(), da.getState(), da.getPostalCode(), da.getCountry());
                if (geocoded != null) {
                    destLat = geocoded.getLatitude();
                    destLon = geocoded.getLongitude();
                }
            }
        } else {
            destLoc.setCustomerName("Consignee Receiving");
            destLoc.setAddress("Commercial Logistics Terminal");
            destLoc.setCity("Chennai");
            destLoc.setState("Tamil Nadu");
            destLoc.setPostalCode("600001");
        }
        destLoc.setLatitude(destLat);
        destLoc.setLongitude(destLon);
        destLoc.setCoordinates(new double[]{destLat, destLon});
        dto.setDestination(destLoc);

        // 3. Current Live / Telemetry Position
        double currentLat = shipment.getCurrentLatitude() != null ? shipment.getCurrentLatitude() : origLat;
        double currentLon = shipment.getCurrentLongitude() != null ? shipment.getCurrentLongitude() : origLon;
        DriverLocationDTO currLoc = new DriverLocationDTO();
        currLoc.setFacilityName(shipment.getCurrentLocationName() != null ? shipment.getCurrentLocationName() : "In Transit Corridor");
        currLoc.setLatitude(currentLat);
        currLoc.setLongitude(currentLon);
        currLoc.setCoordinates(new double[]{currentLat, currentLon});
        currLoc.setCity(destLoc.getCity());
        dto.setCurrentLocation(currLoc);

        // 4. GPS Freshness
        if (shipment.getLastLocationUpdatedAt() != null) {
            dto.setLastLocationUpdatedAt(shipment.getLastLocationUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            java.time.Duration diff = java.time.Duration.between(shipment.getLastLocationUpdatedAt(), LocalDateTime.now());
            if (Math.abs(diff.getSeconds()) < 60) {
                dto.setFreshness("LIVE");
            } else if (Math.abs(diff.toMinutes()) < 5) {
                dto.setFreshness("RECENT");
            } else {
                dto.setFreshness("LAST_RECORDED");
            }
        } else {
            dto.setFreshness("LAST_RECORDED");
        }

        // 5. Road Route Calculation via RoadRoutingService
        RoadRoutingService.RouteCalculationResult currentToDest = roadRoutingService.calculateRoute(currentLat, currentLon, destLat, destLon);
        RoadRoutingService.RouteCalculationResult fullRoute = roadRoutingService.calculateRoute(origLat, origLon, destLat, destLon);

        dto.setDistanceRemainingKm(currentToDest.getDistanceKm());
        dto.setTotalDistanceKm(fullRoute.getDistanceKm());
        dto.setEstimatedDurationMinutes(currentToDest.getDurationMinutes());
        dto.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(currentToDest.getDurationMinutes()).format(DateTimeFormatter.ofPattern("hh:mm a")));
        dto.setRouteCode("RT-" + (origWarehouse != null ? origWarehouse.getWarehouseCode() : "HUB") + "-" + (da != null && da.getCity() != null ? da.getCity().toUpperCase().replaceAll("[^A-Z]", "") : "DEST"));

        dto.setRouteGeometry(currentToDest.getGeometry());
        dto.setFullRouteGeometry(fullRoute.getGeometry());
        dto.setRouteSteps(currentToDest.getSteps());

        // 6. Dynamic Waypoints
        List<DriverRouteWaypointDTO> waypoints = new ArrayList<>();
        boolean isDelivered = shipment.getStatus() == OrderStatus.DELIVERED;
        boolean isDispatched = shipment.getStatus() == OrderStatus.IN_TRANSIT || shipment.getStatus() == OrderStatus.OUT_FOR_DELIVERY;

        waypoints.add(new DriverRouteWaypointDTO(
                "WP-ORIGIN",
                origLoc.getFacilityName(),
                "ORIGIN",
                origLoc.getAddress() + ", " + origLoc.getCity(),
                origLat,
                origLon,
                0.0,
                "Departed",
                isDispatched || isDelivered ? "COMPLETED" : "NEXT",
                "Outbound loading & dispatch clearance completed"
        ));

        if (isDispatched && !isDelivered) {
            double coveredKm = Math.max(0.0, Math.round((fullRoute.getDistanceKm() - currentToDest.getDistanceKm()) * 10.0) / 10.0);
            waypoints.add(new DriverRouteWaypointDTO(
                    "WP-CURRENT",
                    currLoc.getFacilityName() != null ? currLoc.getFacilityName() : "Current Live Position",
                    "TRANSIT",
                    "Active Commercial Transit Corridor",
                    currentLat,
                    currentLon,
                    coveredKm,
                    "Now",
                    "NEXT",
                    "Real-time vehicle telemetry stream active"
            ));
        }

        waypoints.add(new DriverRouteWaypointDTO(
                "WP-DEST",
                destLoc.getCustomerName() != null ? destLoc.getCustomerName() + " Receiving" : "Customer Destination",
                "DESTINATION",
                destLoc.getAddress() + ", " + destLoc.getCity(),
                destLat,
                destLon,
                fullRoute.getDistanceKm(),
                dto.getEstimatedDeliveryTime(),
                isDelivered ? "COMPLETED" : "UPCOMING",
                "Consignee proof-of-delivery checkpoint"
        ));

        dto.setWaypoints(waypoints);
        return dto;
    }

}
