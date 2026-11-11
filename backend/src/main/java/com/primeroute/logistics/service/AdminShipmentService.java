package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.dto.driver.ProofOfDeliveryDTO;
import com.primeroute.logistics.repository.ProofOfDeliveryRepository;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.DriverRepository;
import com.primeroute.logistics.repository.OrderRepository;
import com.primeroute.logistics.repository.ShipmentRepository;
import com.primeroute.logistics.repository.ShipmentTrackingEventRepository;
import com.primeroute.logistics.repository.VehicleRepository;
import com.primeroute.logistics.repository.WarehouseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminShipmentService {

    private static final Logger log = LoggerFactory.getLogger(AdminShipmentService.class);

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTrackingEventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final NotificationService notificationService;
    private final GeocodingService geocodingService;
    private final WarehouseRepository warehouseRepository;
    private final ProofOfDeliveryRepository proofOfDeliveryRepository;

    public AdminShipmentService(
            ShipmentRepository shipmentRepository,
            ShipmentTrackingEventRepository eventRepository,
            OrderRepository orderRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            NotificationService notificationService,
            GeocodingService geocodingService,
            WarehouseRepository warehouseRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) ProofOfDeliveryRepository proofOfDeliveryRepository) {
        this.shipmentRepository = shipmentRepository;
        this.eventRepository = eventRepository;
        this.orderRepository = orderRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.notificationService = notificationService;
        this.geocodingService = geocodingService;
        this.warehouseRepository = warehouseRepository;
        this.proofOfDeliveryRepository = proofOfDeliveryRepository;
    }

    // ==========================================
    // 1. GET ALL SHIPMENTS (OPTIMIZED SINGLE QUERY)
    // ==========================================
    @Transactional(readOnly = true)
    public List<AdminShipmentDTO> getAllShipments() {
        List<Shipment> shipments = shipmentRepository.findAllWithDetails();
        return shipments.stream().map(s -> convertToDto(s, false)).collect(Collectors.toList());
    }

    // ==========================================
    // 2. GET SHIPMENT BY ID (WITH FULL MILESTONES)
    // ==========================================
    @Transactional(readOnly = true)
    public AdminShipmentDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + id));
        return convertToDto(shipment, true);
    }

    // ==========================================
    // 3. CREATE SHIPMENT FROM EXISTING ORDER
    // ==========================================
    @Transactional
    public AdminShipmentDTO createShipment(Long orderId, Long warehouseId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot create a shipment for cancelled order " + order.getOrderNumber());
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Cannot create a shipment for already delivered order " + order.getOrderNumber());
        }

        Optional<Shipment> existing = shipmentRepository.findByOrder(order);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A shipment has already been created for order " + order.getOrderNumber() + " (" + existing.get().getTrackingNumber() + ")");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive warehouse " + warehouse.getWarehouseCode() + " for fulfillment.");
        }

        Address pickup = order.getPickupAddress();
        Address delivery = order.getDeliveryAddress();

        ensureCoordinates(pickup);
        ensureCoordinates(delivery);

        // Connect order to the selected fulfillment warehouse
        order.setOriginWarehouse(warehouse);
        orderRepository.save(order);

        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(generateUniqueTrackingNumber());
        shipment.setOrder(order);
        shipment.setOriginWarehouse(warehouse);

        // Driver & Vehicle are NOT assigned during Admin creation (Warehouse Manager operational responsibility)
        shipment.setDriver(null);
        shipment.setVehicle(null);
        shipment.setAssignmentStatus("Awaiting Warehouse Assignment");
        shipment.setAssignedBy("Not Assigned");
        shipment.setAssignedAt(null);

        // Initial shipment status
        OrderStatus initialStatus = order.getStatus();
        if (initialStatus == null || initialStatus == OrderStatus.CANCELLED) {
            initialStatus = OrderStatus.PENDING;
        }
        shipment.setStatus(initialStatus);
        shipment.setOperationalStage("READY_FOR_PROCESSING");

        // Initial location set to fulfillment warehouse
        String locName = warehouse.getWarehouseName() + " (" + warehouse.getWarehouseCode() + ")";
        shipment.setCurrentLocationName(locName);
        if (warehouse.getLatitude() != null && warehouse.getLongitude() != null) {
            shipment.setCurrentLatitude(warehouse.getLatitude());
            shipment.setCurrentLongitude(warehouse.getLongitude());
        } else if (pickup != null) {
            shipment.setCurrentLatitude(pickup.getLatitude());
            shipment.setCurrentLongitude(pickup.getLongitude());
        }

        LocalDate estimatedDate = calculateEstimatedDeliveryDate(order);
        shipment.setEstimatedDeliveryDate(estimatedDate);

        LocalDateTime now = LocalDateTime.now();
        shipment.setCreatedAt(now);
        shipment.setUpdatedAt(now);

        Shipment savedShipment = shipmentRepository.save(shipment);
        log.info("Admin created new shipment {} for order {} at fulfillment warehouse {}",
                savedShipment.getTrackingNumber(), order.getOrderNumber(), warehouse.getWarehouseCode());

        // Baseline milestone event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                savedShipment,
                savedShipment.getStatus(),
                "Shipment Created from Order #" + order.getOrderNumber(),
                warehouse.getWarehouseName(),
                "Shipment registered and assigned to fulfillment warehouse " + warehouse.getWarehouseName() + " (" + warehouse.getWarehouseCode() + "). Awaiting warehouse manager operational processing."
        );
        eventRepository.save(event);

        // Notifications
        try {
            if (order.getCustomer() != null) {
                notificationService.createNotification(
                        order.getCustomer(),
                        "Shipment Created",
                        "Your shipment #" + savedShipment.getTrackingNumber() + " for order #" + order.getOrderNumber() + " has been booked for fulfillment at " + warehouse.getWarehouseName() + ".",
                        "SHIPMENT"
                );
            }
            notificationService.notifyAdmins(
                    "Shipment Created",
                    "Shipment #" + savedShipment.getTrackingNumber() + " created for order #" + order.getOrderNumber() + " at " + warehouse.getWarehouseCode() + ".",
                    "SHIPMENT_UPDATE"
            );
        } catch (Exception ex) {
            log.warn("Failed to notify for new shipment {}: {}", savedShipment.getTrackingNumber(), ex.getMessage());
        }

        return convertToDto(savedShipment, true);
    }

    public AdminShipmentDTO assignVehicle(Long shipmentId, Long vehicleId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if (vehicleId == null) {
            // Unassigning vehicle
            Vehicle prevVehicle = shipment.getVehicle();
            shipment.setVehicle(null);
            shipment.setUpdatedAt(LocalDateTime.now());
            Shipment saved = shipmentRepository.save(shipment);

            if (prevVehicle != null) {
                List<Shipment> remainingActive = shipmentRepository.findActiveShipmentsByVehicle(prevVehicle);
                if (remainingActive.isEmpty() && prevVehicle.getStatus() != VehicleStatus.INACTIVE && prevVehicle.getStatus() != VehicleStatus.MAINTENANCE) {
                    prevVehicle.setStatus(prevVehicle.getAssignedDriver() != null ? VehicleStatus.ASSIGNED : VehicleStatus.AVAILABLE);
                    vehicleRepository.save(prevVehicle);
                }
            }
            log.info("Vehicle unassigned from shipment {}", shipment.getTrackingNumber());
            return convertToDto(saved, true);
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (vehicle.getStatus() == VehicleStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive vehicle " + vehicle.getVehicleNumber() + " to shipment.");
        }
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new IllegalArgumentException("Cannot assign vehicle " + vehicle.getVehicleNumber() + " undergoing maintenance.");
        }

        Driver assignedDriver = vehicle.getAssignedDriver();
        if (assignedDriver == null) {
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " does not have an assigned driver.");
        }

        // Business rule check: A vehicle should not have more than one active shipment at the same time.
        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
        boolean hasOtherActive = activeShipments.stream().anyMatch(s -> !s.getId().equals(shipmentId));
        if (hasOtherActive) {
            Shipment activeOnVeh = activeShipments.stream().filter(s -> !s.getId().equals(shipmentId)).findFirst().get();
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is currently assigned to an active shipment (" + activeOnVeh.getTrackingNumber() + ").");
        }

        // Driver consistency check:
        if (shipment.getDriver() != null) {
            if (!assignedDriver.getId().equals(shipment.getDriver().getId())) {
                throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is assigned to " + assignedDriver.getFullName() + " and cannot be assigned to another driver.");
            }
        } else {
            // Auto-bind vehicle's assigned driver to the shipment
            List<Shipment> activeDriverShipments = shipmentRepository.findActiveShipmentsByDriver(assignedDriver);
            boolean hasOtherDriverActive = activeDriverShipments.stream().anyMatch(s -> !s.getId().equals(shipmentId));
            if (hasOtherDriverActive) {
                Shipment activeOnDriver = activeDriverShipments.stream().filter(s -> !s.getId().equals(shipmentId)).findFirst().get();
                throw new IllegalArgumentException("Driver " + assignedDriver.getFullName() + " is currently assigned to an active shipment (" + activeOnDriver.getTrackingNumber() + ").");
            }
            shipment.setDriver(assignedDriver);
            if (shipment.getStatus() != OrderStatus.DELIVERED && shipment.getStatus() != OrderStatus.CANCELLED) {
                assignedDriver.setStatus(DriverStatus.ON_TRIP);
                driverRepository.save(assignedDriver);
            }
        }

        // Previous vehicle cleanup if reassigning
        Vehicle prevVehicle = shipment.getVehicle();
        if (prevVehicle != null && !prevVehicle.getId().equals(vehicle.getId())) {
            List<Shipment> remaining = shipmentRepository.findActiveShipmentsByVehicle(prevVehicle);
            boolean prevHasOther = remaining.stream().anyMatch(s -> !s.getId().equals(shipmentId));
            if (!prevHasOther && prevVehicle.getStatus() != VehicleStatus.INACTIVE && prevVehicle.getStatus() != VehicleStatus.MAINTENANCE) {
                prevVehicle.setStatus(prevVehicle.getAssignedDriver() != null ? VehicleStatus.ASSIGNED : VehicleStatus.AVAILABLE);
                vehicleRepository.save(prevVehicle);
            }
        }

        shipment.setVehicle(vehicle);
        shipment.setUpdatedAt(LocalDateTime.now());

        // Status synchronization:
        if (shipment.getStatus() != OrderStatus.DELIVERED && shipment.getStatus() != OrderStatus.CANCELLED) {
            if (shipment.getStatus() == OrderStatus.IN_TRANSIT || shipment.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                vehicle.setStatus(VehicleStatus.IN_TRANSIT);
            } else if (vehicle.getStatus() == VehicleStatus.AVAILABLE) {
                vehicle.setStatus(VehicleStatus.ASSIGNED);
            }
            vehicleRepository.save(vehicle);
        }

        Shipment saved = shipmentRepository.save(shipment);
        log.info("Assigned vehicle {} to shipment {}", vehicle.getVehicleNumber(), saved.getTrackingNumber());
        return convertToDto(saved, true);
    }

    public AdminShipmentDTO assignDriver(Long shipmentId, Long driverId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        Driver prevDriver = shipment.getDriver();

        if (driverId == null) {
            // Unassigning driver
            shipment.setDriver(null);
            shipment.setUpdatedAt(LocalDateTime.now());
            Shipment saved = shipmentRepository.save(shipment);

            if (prevDriver != null) {
                List<Shipment> remainingActive = shipmentRepository.findActiveShipmentsByDriver(prevDriver);
                if (remainingActive.isEmpty() && prevDriver.getStatus() != DriverStatus.INACTIVE) {
                    prevDriver.setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(prevDriver);
                }
            }
            log.info("Driver unassigned from shipment {}", shipment.getTrackingNumber());
            return convertToDto(saved, true);
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

        if (driver.getStatus() == DriverStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive driver " + driver.getFullName() + " to shipment.");
        }

        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByDriver(driver);
        boolean hasOtherActive = activeShipments.stream().anyMatch(s -> !s.getId().equals(shipmentId));
        if (hasOtherActive) {
            Shipment activeOnDriver = activeShipments.stream().filter(s -> !s.getId().equals(shipmentId)).findFirst().get();
            throw new IllegalArgumentException("Driver " + driver.getFullName() + " is currently assigned to an active shipment (" + activeOnDriver.getTrackingNumber() + ").");
        }

        // If shipment already has a vehicle assigned, enforce vehicle's assigned driver match
        if (shipment.getVehicle() != null) {
            Vehicle v = shipment.getVehicle();
            if (v.getAssignedDriver() == null) {
                throw new IllegalArgumentException("Vehicle " + v.getVehicleNumber() + " does not have an assigned driver.");
            }
            if (!v.getAssignedDriver().getId().equals(driver.getId())) {
                throw new IllegalArgumentException("Vehicle " + v.getVehicleNumber() + " is assigned to " + v.getAssignedDriver().getFullName() + " and cannot be assigned to another driver.");
            }
        }

        shipment.setDriver(driver);
        shipment.setUpdatedAt(LocalDateTime.now());

        if (shipment.getStatus() != OrderStatus.DELIVERED && shipment.getStatus() != OrderStatus.CANCELLED) {
            driver.setStatus(DriverStatus.ON_TRIP);
            driverRepository.save(driver);
        }

        if (prevDriver != null && !prevDriver.getId().equals(driver.getId())) {
            List<Shipment> remainingActive = shipmentRepository.findActiveShipmentsByDriver(prevDriver);
            boolean prevHasOther = remainingActive.stream().anyMatch(s -> !s.getId().equals(shipmentId));
            if (!prevHasOther && prevDriver.getStatus() != DriverStatus.INACTIVE) {
                prevDriver.setStatus(DriverStatus.AVAILABLE);
                driverRepository.save(prevDriver);
            }
        }

        Shipment saved = shipmentRepository.save(shipment);
        log.info("Driver {} (ID: {}) assigned to shipment {}", driver.getFullName(), driver.getId(), shipment.getTrackingNumber());
        return convertToDto(saved, true);
    }

    public AdminShipmentDTO updateShipment(Long id, AdminUpdateShipmentRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + id));

        if (request.getStatus() != null) {
            shipment.setStatus(request.getStatus());
            if (shipment.getOrder() != null) {
                shipment.getOrder().setStatus(request.getStatus());
                shipment.getOrder().setUpdatedAt(LocalDateTime.now());
                orderRepository.save(shipment.getOrder());
            }

            if (request.getStatus() == OrderStatus.DELIVERED || request.getStatus() == OrderStatus.CANCELLED) {
                if (shipment.getDriver() != null && shipment.getDriver().getStatus() != DriverStatus.INACTIVE) {
                    Driver driver = shipment.getDriver();
                    List<Shipment> remaining = shipmentRepository.findActiveShipmentsByDriver(driver);
                    boolean hasOther = remaining.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
                    if (!hasOther) {
                        driver.setStatus(DriverStatus.AVAILABLE);
                        driverRepository.save(driver);
                    }
                }
                if (shipment.getVehicle() != null) {
                    Vehicle v = shipment.getVehicle();
                    List<Shipment> remaining = shipmentRepository.findActiveShipmentsByVehicle(v);
                    boolean hasOther = remaining.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
                    if (!hasOther && v.getStatus() != VehicleStatus.INACTIVE && v.getStatus() != VehicleStatus.MAINTENANCE) {
                        v.setStatus(v.getAssignedDriver() != null ? VehicleStatus.ASSIGNED : VehicleStatus.AVAILABLE);
                        vehicleRepository.save(v);
                    }
                }
            } else if (request.getStatus() == OrderStatus.IN_TRANSIT || request.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                if (shipment.getVehicle() != null) {
                    Vehicle v = shipment.getVehicle();
                    if (v.getStatus() != VehicleStatus.INACTIVE && v.getStatus() != VehicleStatus.MAINTENANCE) {
                        v.setStatus(VehicleStatus.IN_TRANSIT);
                        vehicleRepository.save(v);
                    }
                }
            }
        }

        if (request.getCurrentLocationName() != null && !request.getCurrentLocationName().isBlank()) {
            shipment.setCurrentLocationName(request.getCurrentLocationName().trim());
        }

        if (request.getEstimatedDeliveryDate() != null) {
            shipment.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        }

        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment updated = shipmentRepository.save(shipment);
        return convertToDto(updated, false);
    }

    @Transactional
    public AdminShipmentDTO updateShipmentStatus(Long id, OrderStatus newStatus, String locationName, String description) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + id));

        OrderStatus oldStatus = shipment.getStatus();
        shipment.setStatus(newStatus);
        if (locationName != null && !locationName.isBlank()) {
            shipment.setCurrentLocationName(locationName.trim());
        }
        shipment.setUpdatedAt(LocalDateTime.now());

        // Sync order status
        if (shipment.getOrder() != null) {
            Order order = shipment.getOrder();
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
        }

        // If shipment is DELIVERED or CANCELLED, free the assigned driver and vehicle back to AVAILABLE if no other active shipments
        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED) {
            if (shipment.getDriver() != null) {
                Driver driver = shipment.getDriver();
                List<Shipment> remainingActive = shipmentRepository.findActiveShipmentsByDriver(driver);
                boolean hasOtherActive = remainingActive.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
                if (!hasOtherActive && driver.getStatus() != DriverStatus.INACTIVE) {
                    driver.setStatus(DriverStatus.AVAILABLE);
                    driverRepository.save(driver);
                    log.info("Driver {} (ID: {}) transitioned back to AVAILABLE as shipment {} completed.",
                            driver.getFullName(), driver.getId(), shipment.getTrackingNumber());
                }
            }
            if (shipment.getVehicle() != null) {
                Vehicle vehicle = shipment.getVehicle();
                List<Shipment> remainingActiveVeh = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
                boolean hasOtherVehActive = remainingActiveVeh.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
                if (!hasOtherVehActive && vehicle.getStatus() != VehicleStatus.INACTIVE && vehicle.getStatus() != VehicleStatus.MAINTENANCE) {
                    vehicle.setStatus(vehicle.getAssignedDriver() != null ? VehicleStatus.ASSIGNED : VehicleStatus.AVAILABLE);
                    vehicleRepository.save(vehicle);
                    log.info("Vehicle {} (ID: {}) transitioned as shipment {} completed.",
                            vehicle.getVehicleNumber(), vehicle.getId(), shipment.getTrackingNumber());
                }
            }
        }

        // Tracking milestone event
        String eventTitle = "Shipment Status Updated to " + newStatus.name();
        String loc = locationName != null && !locationName.isBlank() ? locationName.trim() : (shipment.getCurrentLocationName() != null ? shipment.getCurrentLocationName() : "In Transit Hub");
        String desc = description != null && !description.isBlank() ? description.trim() : ("Status transitioned from " + oldStatus + " to " + newStatus);

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                newStatus,
                eventTitle,
                loc,
                desc
        );
        eventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);
        return convertToDto(saved, true);
    }

    // ==========================================
    // 7. GET UNASSIGNED ORDERS (WITHOUT SHIPMENT)
    // ==========================================
    @Transactional(readOnly = true)
    public List<AdminOrderResponseDTO> getUnassignedOrders() {
        List<Order> eligibleOrders = orderRepository.findEligibleOrdersForShipment();
        return eligibleOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.PICKED_UP)
                .filter(order -> shipmentRepository.findByOrder(order).isEmpty())
                .map(this::convertOrderToDto)
                .collect(Collectors.toList());
    }


    // ==========================================
    // 4C. ASSIGN / CHANGE WAREHOUSE FOR SHIPMENT (STEP 3)
    // ==========================================
    public AdminShipmentDTO assignWarehouse(Long shipmentId, Long warehouseId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if (warehouseId == null) {
            shipment.setOriginWarehouse(null);
            if (shipment.getOrder() != null) {
                shipment.getOrder().setOriginWarehouse(null);
            }
            shipment.setUpdatedAt(LocalDateTime.now());
            Shipment saved = shipmentRepository.save(shipment);
            log.info("Warehouse unassigned from shipment {}", shipment.getTrackingNumber());
            return convertToDto(saved, true);
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        if (warehouse.getStatus() != WarehouseStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive warehouse " + warehouse.getWarehouseName() + " to shipment.");
        }

        shipment.setOriginWarehouse(warehouse);
        if (shipment.getOrder() != null) {
            shipment.getOrder().setOriginWarehouse(warehouse);
        }
        shipment.setUpdatedAt(LocalDateTime.now());

        // Create milestone tracking event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus(),
                "Assigned to Origin Warehouse: " + warehouse.getWarehouseName(),
                warehouse.getCity(),
                "Shipment assigned to origin warehouse " + warehouse.getWarehouseCode() + " (" + warehouse.getWarehouseName() + ") for warehouse operations."
        );
        eventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);
        log.info("Shipment {} assigned to origin warehouse {} ({})", shipment.getTrackingNumber(), warehouse.getWarehouseCode(), warehouse.getWarehouseName());
        return convertToDto(saved, true);
    }


    // ==========================================
    // 4D. ADMIN OVERRIDE FLEET ASSIGNMENT
    // ==========================================
    public AdminShipmentDTO overrideAssignment(Long shipmentId, AdminOverrideAssignmentRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with ID: " + shipmentId));

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Override reason is mandatory for administrator fleet reassignment.");
        }

        if (request.getDriverId() == null) {
            throw new IllegalArgumentException("Driver ID is required for fleet assignment override.");
        }
        if (request.getVehicleId() == null) {
            throw new IllegalArgumentException("Vehicle ID is required for fleet assignment override.");
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));

        if (driver.getStatus() == DriverStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive driver " + driver.getFullName() + ".");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        if (vehicle.getStatus() == VehicleStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive vehicle " + vehicle.getVehicleNumber() + ".");
        }
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new IllegalArgumentException("Cannot assign vehicle " + vehicle.getVehicleNumber() + " undergoing maintenance.");
        }

        // Validate vehicle assigned driver match
        if (vehicle.getAssignedDriver() != null && !vehicle.getAssignedDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is configured for driver " + vehicle.getAssignedDriver().getFullName() + " and cannot be assigned to another driver.");
        }

        // Active shipment checks
        List<Shipment> activeOnDriver = shipmentRepository.findActiveShipmentsByDriver(driver);
        boolean driverHasOther = activeOnDriver.stream().anyMatch(s -> !s.getId().equals(shipmentId));
        if (driverHasOther) {
            Shipment other = activeOnDriver.stream().filter(s -> !s.getId().equals(shipmentId)).findFirst().get();
            throw new IllegalArgumentException("Driver " + driver.getFullName() + " is currently on trip with shipment " + other.getTrackingNumber() + ".");
        }

        List<Shipment> activeOnVeh = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
        boolean vehHasOther = activeOnVeh.stream().anyMatch(s -> !s.getId().equals(shipmentId));
        if (vehHasOther) {
            Shipment other = activeOnVeh.stream().filter(s -> !s.getId().equals(shipmentId)).findFirst().get();
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is currently assigned to active shipment " + other.getTrackingNumber() + ".");
        }

        // Reclaim previous driver if different
        Driver prevDriver = shipment.getDriver();
        if (prevDriver != null && !prevDriver.getId().equals(driver.getId())) {
            List<Shipment> otherDriverShipments = shipmentRepository.findActiveShipmentsByDriver(prevDriver);
            boolean prevDriverHasOther = otherDriverShipments.stream().anyMatch(s -> !s.getId().equals(shipmentId));
            if (!prevDriverHasOther && prevDriver.getStatus() != DriverStatus.INACTIVE) {
                prevDriver.setStatus(DriverStatus.AVAILABLE);
                driverRepository.save(prevDriver);
            }
        }

        // Reclaim previous vehicle if different
        Vehicle prevVehicle = shipment.getVehicle();
        if (prevVehicle != null && !prevVehicle.getId().equals(vehicle.getId())) {
            List<Shipment> otherVehShipments = shipmentRepository.findActiveShipmentsByVehicle(prevVehicle);
            boolean prevVehHasOther = otherVehShipments.stream().anyMatch(s -> !s.getId().equals(shipmentId));
            if (!prevVehHasOther && prevVehicle.getStatus() != VehicleStatus.INACTIVE && prevVehicle.getStatus() != VehicleStatus.MAINTENANCE) {
                prevVehicle.setStatus(prevVehicle.getAssignedDriver() != null ? VehicleStatus.ASSIGNED : VehicleStatus.AVAILABLE);
                vehicleRepository.save(prevVehicle);
            }
        }

        // Apply new assignment
        shipment.setDriver(driver);
        shipment.setVehicle(vehicle);
        shipment.setAssignedBy("Admin Override");
        shipment.setAssignedAt(LocalDateTime.now());
        shipment.setAssignmentStatus("ASSIGNED");
        shipment.setUpdatedAt(LocalDateTime.now());

        driver.setStatus(DriverStatus.ON_TRIP);
        driverRepository.save(driver);

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        // Record tracking milestone event
        String prevDriverName = prevDriver != null ? prevDriver.getFullName() : "None";
        String prevVehNumber = prevVehicle != null ? prevVehicle.getVehicleNumber() : "None";
        String description = "Fleet Reassignment Override by Administrator. Previous: Driver [" + prevDriverName + "], Vehicle [" + prevVehNumber + "] -> New: Driver [" + driver.getFullName() + "], Vehicle [" + vehicle.getVehicleNumber() + "]. Reason: " + request.getReason().trim();

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus(),
                "Fleet Assignment Overridden by Admin",
                shipment.getOriginWarehouse() != null ? shipment.getOriginWarehouse().getWarehouseName() : "Admin Operations Hub",
                description
        );
        eventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);
        log.info("Shipment {} fleet overridden by Admin: Driver={}, Vehicle={}, Reason={}", shipment.getTrackingNumber(), driver.getFullName(), vehicle.getVehicleNumber(), request.getReason());
        return convertToDto(saved, true);
    }

    // ==========================================
    // DTO CONVERTERS
    // ==========================================
    private AdminShipmentDTO convertToDto(Shipment s, boolean includeEvents) {
        AdminShipmentDTO dto = new AdminShipmentDTO();
        dto.setId(s.getId());
        dto.setTrackingNumber(s.getTrackingNumber());
        dto.setShipmentNumber(s.getTrackingNumber());
        if (s.getOriginWarehouse() != null) {
            Warehouse wh = s.getOriginWarehouse();
            dto.setOriginWarehouseId(wh.getId());
            dto.setOriginWarehouseCode(wh.getWarehouseCode());
            dto.setOriginWarehouseName(wh.getWarehouseName());
            dto.setOriginWarehouseManagerName(wh.getManagerName() != null ? wh.getManagerName() : "Not Assigned");
            dto.setOriginWarehouseManagerEmail(wh.getManagerEmail() != null ? wh.getManagerEmail() : "");
            dto.setWarehouseId(wh.getId());
            dto.setWarehouseCode(wh.getWarehouseCode());
            dto.setWarehouseName(wh.getWarehouseName());
            dto.setWarehouseManagerName(wh.getManagerName() != null ? wh.getManagerName() : "Not Assigned");
            dto.setWarehouseManagerEmail(wh.getManagerEmail() != null ? wh.getManagerEmail() : "");
        }
        if (s.getDestinationWarehouse() != null) {
            Warehouse dwh = s.getDestinationWarehouse();
            dto.setDestinationWarehouseId(dwh.getId());
            dto.setDestinationWarehouseCode(dwh.getWarehouseCode());
            dto.setDestinationWarehouseName(dwh.getWarehouseName());
        }
        dto.setStatus(s.getStatus());
        dto.setExpectedDelivery(s.getEstimatedDeliveryDate());
        dto.setCurrentLocation(s.getCurrentLocationName() != null ? s.getCurrentLocationName() : "In Transit Hub");
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());

        // Driver details
        if (s.getDriver() != null) {
            Driver d = s.getDriver();
            dto.setDriverId(d.getId());
            dto.setAssignedDriver(d.getFullName());
            dto.setDriverPhone(d.getPhone());
            dto.setDriverStatus(d.getStatus());
        } else {
            dto.setAssignedDriver(null);
        }

        // Vehicle details
        if (s.getVehicle() != null) {
            Vehicle v = s.getVehicle();
            dto.setVehicleId(v.getId());
            dto.setVehicleNumber(v.getVehicleNumber());
            dto.setVehicleModel(v.getModel());
            dto.setVehicleType(v.getVehicleType() != null ? v.getVehicleType().name() : null);
            dto.setVehicleRegistrationNumber(v.getRegistrationNumber());
            dto.setAssignedVehicle(v.getVehicleNumber() + " - " + v.getModel());
        } else {
            dto.setVehicleId(null);
            dto.setVehicleNumber(null);
            dto.setVehicleModel(null);
            dto.setVehicleType(null);
            dto.setVehicleRegistrationNumber(null);
            dto.setAssignedVehicle(null);
        }

        // Assignment Status & Audit (Preserve historical assignment facts)
        if (s.getStatus() == OrderStatus.DELIVERED) {
            dto.setAssignmentStatus("Completed / Delivered");
        } else if (s.getDriver() != null || s.getVehicle() != null) {
            dto.setAssignmentStatus(s.getAssignmentStatus() != null ? s.getAssignmentStatus() : "ASSIGNED");
        } else {
            dto.setAssignmentStatus(s.getAssignmentStatus() != null ? s.getAssignmentStatus() : "Awaiting Warehouse Assignment");
        }

        if (s.getAssignedBy() != null && !s.getAssignedBy().isBlank()) {
            dto.setAssignedBy(s.getAssignedBy());
        } else if (s.getDriver() != null || s.getVehicle() != null || s.getStatus() == OrderStatus.DELIVERED) {
            Warehouse wh = s.getOriginWarehouse();
            dto.setAssignedBy(wh != null && wh.getManagerName() != null ? wh.getManagerName() : "Warehouse Operations");
        } else {
            dto.setAssignedBy("Not Assigned");
        }

        if (s.getAssignedAt() != null) {
            dto.setAssignedAt(s.getAssignedAt());
        } else if (s.getDriver() != null || s.getVehicle() != null || s.getStatus() == OrderStatus.DELIVERED) {
            dto.setAssignedAt(s.getCreatedAt());
        } else {
            dto.setAssignedAt(null);
        }

        if (proofOfDeliveryRepository != null) {
            proofOfDeliveryRepository.findByShipment(s).ifPresent(pod -> {
                ProofOfDeliveryDTO podDto = new ProofOfDeliveryDTO();
                podDto.setId(pod.getId());
                podDto.setShipmentId(s.getId());
                podDto.setTrackingNumber(s.getTrackingNumber());
                podDto.setRecipientName(pod.getRecipientName());
                podDto.setReceivedBy(pod.getReceivedBy());
                podDto.setSignatureData(pod.getSignatureData());
                podDto.setSignatureBase64(pod.getSignatureData());
                podDto.setPhotoData(pod.getPhotoData());
                podDto.setPhotoUrl(pod.getPhotoData());
                podDto.setDeliveryNotes(pod.getDeliveryNotes());
                podDto.setDeliveredBy(pod.getDeliveredBy());
                if (pod.getDeliveredAt() != null) {
                    podDto.setDeliveredAt(pod.getDeliveredAt().toString());
                }
                podDto.setLatitude(pod.getLatitude());
                podDto.setLongitude(pod.getLongitude());
                dto.setProofOfDelivery(podDto);
            });
        }
        Order order = s.getOrder();
        if (order != null) {
            dto.setOrderId(order.getId());
            dto.setOrderNumber(order.getOrderNumber());
            dto.setDeliveryType(order.getDeliveryType());
            dto.setPackageDescription(order.getPackageDescription());
            dto.setPackageWeight(order.getPackageWeight());
            dto.setPackageQuantity(order.getPackageQuantity());

            if (order.getCustomer() != null) {
                User cust = order.getCustomer();
                dto.setCustomerId(cust.getId());
                dto.setCustomerName(cust.getFullName());
                dto.setCustomerEmail(cust.getEmail());
                dto.setCustomerPhone(cust.getPhone());
            }

            Address pickup = order.getPickupAddress();
            if (pickup != null) {
                dto.setPickupAddress(formatAddress(pickup));
                dto.setPickupCity(pickup.getCity());
                dto.setPickupState(pickup.getState());
                dto.setOrigin(pickup.getCity());
            }

            Address delivery = order.getDeliveryAddress();
            if (delivery != null) {
                dto.setDeliveryAddress(formatAddress(delivery));
                dto.setDeliveryCity(delivery.getCity());
                dto.setDeliveryState(delivery.getState());
                dto.setDestination(delivery.getCity());
            }
        }

        if (includeEvents) {
            List<ShipmentTrackingEvent> events = eventRepository.findByShipmentOrderByEventTimeAsc(s);
            dto.setEvents(events.stream().map(e -> {
                AdminShipmentTrackingEventDTO eventDto = new AdminShipmentTrackingEventDTO();
                eventDto.setId(e.getId());
                eventDto.setStatus(e.getStatus());
                eventDto.setTitle(e.getTitle());
                eventDto.setDescription(e.getDescription());
                eventDto.setLocationName(e.getLocationName());
                eventDto.setLatitude(e.getLatitude());
                eventDto.setLongitude(e.getLongitude());
                eventDto.setEventTime(e.getEventTime());
                eventDto.setCompleted(isEventCompleted(e.getStatus(), s.getStatus()));
                eventDto.setCurrent(isEventCurrent(e.getStatus(), s.getStatus()));
                return eventDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    private AdminOrderResponseDTO convertOrderToDto(Order order) {
        AdminOrderResponseDTO dto = new AdminOrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());

        User customer = order.getCustomer();
        if (customer != null) {
            dto.setCustomerId(customer.getId());
            dto.setCustomerName(customer.getFullName());
            dto.setCustomerEmail(customer.getEmail());
            dto.setCustomerPhone(customer.getPhone());
        }

        Address p = order.getPickupAddress();
        if (p != null) {
            dto.setPickupAddress(formatAddress(p));
            dto.setPickupCity(p.getCity());
            dto.setPickupState(p.getState());
        }

        Address d = order.getDeliveryAddress();
        if (d != null) {
            dto.setDeliveryAddress(formatAddress(d));
            dto.setDeliveryCity(d.getCity());
            dto.setDeliveryState(d.getState());
        }

        dto.setPackageDescription(order.getPackageDescription());
        dto.setPackageWeight(order.getPackageWeight());
        dto.setPackageQuantity(order.getPackageQuantity());
        dto.setDeliveryType(order.getDeliveryType());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    private String generateUniqueTrackingNumber() {
        String chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
        SecureRandom random = new SecureRandom();
        String code;
        int attempts = 0;
        do {
            StringBuilder sb = new StringBuilder("TRK-PR-");
            for (int i = 0; i < 8; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
            attempts++;
            if (attempts > 50) {
                code = "TRK-PR-" + Long.toHexString(System.currentTimeMillis()).toUpperCase();
                break;
            }
        } while (shipmentRepository.existsByTrackingNumber(code));
        return code;
    }

    private LocalDate calculateEstimatedDeliveryDate(Order order) {
        LocalDate base = order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate() : LocalDate.now();
        if (order.getDeliveryType() != null) {
            return switch (order.getDeliveryType()) {
                case SAME_DAY -> base;
                case EXPRESS -> base.plusDays(1);
                default -> base.plusDays(3);
            };
        }
        return base.plusDays(3);
    }

    private void ensureCoordinates(Address address) {
        if (address != null && (address.getLatitude() == null || address.getLongitude() == null)) {
            GeocodingService.Coordinates coords = geocodingService.geocode(
                    address.getAddressLine1(),
                    address.getCity(),
                    address.getState(),
                    address.getPostalCode(),
                    address.getCountry()
            );
            if (coords != null) {
                address.setLatitude(coords.getLatitude());
                address.setLongitude(coords.getLongitude());
            }
        }
    }

    private String formatAddress(Address a) {
        if (a == null) return "";
        StringBuilder sb = new StringBuilder();
        if (a.getAddressLine1() != null) sb.append(a.getAddressLine1());
        if (a.getAddressLine2() != null && !a.getAddressLine2().isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(a.getAddressLine2());
        }
        if (a.getCity() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(a.getCity());
        }
        if (a.getState() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(a.getState());
        }
        if (a.getPostalCode() != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(a.getPostalCode());
        }
        return sb.toString();
    }

    private String formatEventTitle(OrderStatus status) {
        if (status == null) return "Checkpoint Update";
        return switch (status) {
            case PENDING -> "Order Registered";
            case CONFIRMED -> "Booking Confirmed";
            case PICKED_UP -> "Cargo Picked Up";
            case IN_TRANSIT -> "In Transit Hub";
            case OUT_FOR_DELIVERY -> "Out for Final Delivery";
            case DELIVERED -> "Consignment Delivered";
            case CANCELLED -> "Shipment Cancelled";
        };
    }

    private void generateMilestoneEvents(Shipment shipment, Order order) {
        LocalDateTime baseTime = shipment.getCreatedAt() != null ? shipment.getCreatedAt() : LocalDateTime.now();
        Address pickup = order.getPickupAddress();
        Address delivery = order.getDeliveryAddress();

        String originCity = pickup != null && pickup.getCity() != null ? pickup.getCity() : "Origin";
        String destCity = delivery != null && delivery.getCity() != null ? delivery.getCity() : "Destination";

        Double originLat = pickup != null ? pickup.getLatitude() : null;
        Double originLng = pickup != null ? pickup.getLongitude() : null;
        Double destLat = delivery != null ? delivery.getLatitude() : null;
        Double destLng = delivery != null ? delivery.getLongitude() : null;

        Double midLat = (originLat != null && destLat != null) ? (originLat + destLat) / 2.0 : originLat;
        Double midLng = (originLng != null && destLng != null) ? (originLng + destLng) / 2.0 : originLng;

        eventRepository.save(new ShipmentTrackingEvent(
                shipment,
                "ORDER_CREATED",
                "Order Created & Confirmed",
                "Booking reference " + order.getOrderNumber() + " registered and logistics order confirmed.",
                originCity + " Origin Terminal",
                originLat,
                originLng,
                baseTime
        ));

        eventRepository.save(new ShipmentTrackingEvent(
                shipment,
                "PICKED_UP",
                "Cargo Picked Up",
                order.getPackageDescription() + " (" + order.getPackageWeight() + " kg) received at origin fulfillment facility.",
                originCity + " Sorting Facility",
                originLat,
                originLng,
                baseTime.plusHours(2)
        ));

        eventRepository.save(new ShipmentTrackingEvent(
                shipment,
                "IN_TRANSIT",
                "In Transit Hub",
                "Consignment processed and routed through express freight linehaul corridor.",
                "Regional Transit Hub (" + originCity + " to " + destCity + ")",
                midLat,
                midLng,
                baseTime.plusHours(8)
        ));

        eventRepository.save(new ShipmentTrackingEvent(
                shipment,
                "OUT_FOR_DELIVERY",
                "Out for Final Delivery",
                "Consignment assigned to regional delivery executive for doorstep handover.",
                destCity + " Distribution Depot",
                destLat,
                destLng,
                baseTime.plusHours(20)
        ));

        eventRepository.save(new ShipmentTrackingEvent(
                shipment,
                "DELIVERED",
                "Delivered to Destination",
                "Parcel successfully delivered and verified by recipient signature.",
                destCity + " Destination",
                destLat,
                destLng,
                baseTime.plusHours(24)
        ));
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
