package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.AdminRouteDTO;
import com.primeroute.logistics.dto.CreateRouteRequest;
import com.primeroute.logistics.dto.UpdateRouteRequest;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.exception.AssignmentConflictException;

import com.primeroute.logistics.repository.DriverRepository;
import com.primeroute.logistics.repository.RouteRepository;
import com.primeroute.logistics.repository.ShipmentRepository;
import com.primeroute.logistics.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminRouteService {

    private static final Logger log = LoggerFactory.getLogger(AdminRouteService.class);

    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipmentRepository shipmentRepository;
    private final GeocodingService geocodingService;
    private final NotificationService notificationService;

    public AdminRouteService(RouteRepository routeRepository,
                             DriverRepository driverRepository,
                             VehicleRepository vehicleRepository,
                             ShipmentRepository shipmentRepository,
                             GeocodingService geocodingService,
                             NotificationService notificationService) {
        this.routeRepository = routeRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.shipmentRepository = shipmentRepository;
        this.geocodingService = geocodingService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<AdminRouteDTO> getAllRoutes() {
        return routeRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminRouteDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return convertToDTO(route);
    }

    public AdminRouteDTO createRoute(CreateRouteRequest req) {
        String cleanCode = req.getRouteCode() != null ? req.getRouteCode().trim().toUpperCase() : null;
        if (cleanCode == null || cleanCode.isEmpty()) {
            cleanCode = generateRouteCode(req.getOrigin(), req.getDestination());
        }

        if (routeRepository.existsByRouteCodeIgnoreCase(cleanCode)) {
            throw new IllegalArgumentException("A freight route with code " + cleanCode + " already exists.");
        }

        Route route = new Route();
        route.setRouteCode(cleanCode);
        String rName = req.getRouteName() != null && !req.getRouteName().trim().isEmpty()
                ? req.getRouteName().trim()
                : req.getOrigin().trim() + " → " + req.getDestination().trim();
        route.setRouteName(rName);
        route.setOrigin(req.getOrigin().trim());
        route.setOriginName(req.getOriginName() != null ? req.getOriginName().trim() : req.getOrigin().trim());
        route.setOriginCity(req.getOriginCity() != null ? req.getOriginCity().trim() : null);
        route.setOriginState(req.getOriginState() != null ? req.getOriginState().trim() : null);

        route.setDestination(req.getDestination().trim());
        route.setDestinationName(req.getDestinationName() != null ? req.getDestinationName().trim() : req.getDestination().trim());
        route.setDestinationCity(req.getDestinationCity() != null ? req.getDestinationCity().trim() : null);
        route.setDestinationState(req.getDestinationState() != null ? req.getDestinationState().trim() : null);
        route.setOriginAddress(req.getOriginAddress() != null ? req.getOriginAddress().trim() : req.getOrigin().trim());
        route.setDestinationAddress(req.getDestinationAddress() != null ? req.getDestinationAddress().trim() : req.getDestination().trim());
        Double dist = req.getDistanceKm() != null ? req.getDistanceKm() : req.getDistance();
        route.setDistanceKm(dist);
        Double transit = req.getEstimatedTransitTimeHours() != null ? req.getEstimatedTransitTimeHours() : req.getEstimatedTransitHours();
        route.setEstimatedTransitTimeHours(transit);
        route.setEstimatedTransitHours(transit);

        // Coordinates resolution
        Double origLat = req.getOriginLatitude();
        Double origLng = req.getOriginLongitude();
        if (origLat == null || origLng == null) {
            try {
                GeocodingService.Coordinates coords = geocodingService.geocode(route.getOriginAddress(), route.getOrigin(), null, null, "India");
                if (coords != null) {
                    origLat = coords.getLatitude();
                    origLng = coords.getLongitude();
                }
            } catch (Exception ignored) {}
        }
        route.setOriginLatitude(origLat);
        route.setOriginLongitude(origLng);

        Double destLat = req.getDestinationLatitude();
        Double destLng = req.getDestinationLongitude();
        if (destLat == null || destLng == null) {
            try {
                GeocodingService.Coordinates coords = geocodingService.geocode(route.getDestinationAddress(), route.getDestination(), null, null, "India");
                if (coords != null) {
                    destLat = coords.getLatitude();
                    destLng = coords.getLongitude();
                }
            } catch (Exception ignored) {}
        }
        route.setDestinationLatitude(destLat);
        route.setDestinationLongitude(destLng);

        RouteStatus routeStatus = req.getStatus() != null ? req.getStatus() : RouteStatus.ACTIVE;
        route.setStatus(routeStatus);

        // Optional driver validation
        if (req.getDriverId() != null) {
            Driver driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + req.getDriverId()));
            if (routeStatus == RouteStatus.ACTIVE && routeRepository.existsByDriverIdAndStatus(driver.getId(), RouteStatus.ACTIVE)) {
                throw new AssignmentConflictException("Driver is already assigned to another active route.");
            }
            route.setDriver(driver);
        }

        // Optional vehicle validation
        if (req.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + req.getVehicleId()));
            if (routeStatus == RouteStatus.ACTIVE && routeRepository.existsByVehicleIdAndStatus(vehicle.getId(), RouteStatus.ACTIVE)) {
                throw new AssignmentConflictException("Vehicle is already assigned to another active route.");
            }
            route.setVehicle(vehicle);
        }

        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());

        Route saved = routeRepository.save(route);
        log.info("Admin created new route: {} (ID: {})", saved.getRouteCode(), saved.getId());
        return convertToDTO(saved);
    }

    public AdminRouteDTO updateRoute(Long id, UpdateRouteRequest req) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        String cleanCode = req.getRouteCode() != null && !req.getRouteCode().trim().isEmpty()
                ? req.getRouteCode().trim().toUpperCase()
                : route.getRouteCode();

        if (!cleanCode.equalsIgnoreCase(route.getRouteCode()) && routeRepository.existsByRouteCodeIgnoreCaseAndIdNot(cleanCode, id)) {
            throw new IllegalArgumentException("Another route with code " + cleanCode + " already exists.");
        }

        route.setRouteCode(cleanCode);
        if (req.getRouteName() != null && !req.getRouteName().trim().isEmpty()) {
            route.setRouteName(req.getRouteName().trim());
        }
        route.setOrigin(req.getOrigin().trim());
        if (req.getOriginName() != null) route.setOriginName(req.getOriginName().trim());
        if (req.getOriginCity() != null) route.setOriginCity(req.getOriginCity().trim());
        if (req.getOriginState() != null) route.setOriginState(req.getOriginState().trim());

        route.setDestination(req.getDestination().trim());
        if (req.getDestinationName() != null) route.setDestinationName(req.getDestinationName().trim());
        if (req.getDestinationCity() != null) route.setDestinationCity(req.getDestinationCity().trim());
        if (req.getDestinationState() != null) route.setDestinationState(req.getDestinationState().trim());
        route.setOriginAddress(req.getOriginAddress() != null ? req.getOriginAddress().trim() : route.getOriginAddress());
        route.setDestinationAddress(req.getDestinationAddress() != null ? req.getDestinationAddress().trim() : route.getDestinationAddress());
        Double dist = req.getDistanceKm() != null ? req.getDistanceKm() : req.getDistance();
        route.setDistanceKm(dist);
        Double transit = req.getEstimatedTransitTimeHours() != null ? req.getEstimatedTransitTimeHours() : req.getEstimatedTransitHours();
        route.setEstimatedTransitTimeHours(transit);
        route.setEstimatedTransitHours(transit);
        RouteStatus targetStatus = req.getStatus() != null ? req.getStatus() : route.getStatus();
        route.setStatus(targetStatus);

        // Driver assignment validation
        if (req.getDriverId() != null) {
            Driver driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + req.getDriverId()));
            if (targetStatus == RouteStatus.ACTIVE && routeRepository.existsByDriverIdAndStatusAndIdNot(driver.getId(), RouteStatus.ACTIVE, id)) {
                throw new AssignmentConflictException("Driver is already assigned to another active route.");
            }
            route.setDriver(driver);
        } else {
            route.setDriver(null);
        }

        // Vehicle assignment validation
        if (req.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + req.getVehicleId()));
            if (targetStatus == RouteStatus.ACTIVE && routeRepository.existsByVehicleIdAndStatusAndIdNot(vehicle.getId(), RouteStatus.ACTIVE, id)) {
                throw new AssignmentConflictException("Vehicle is already assigned to another active route.");
            }
            route.setVehicle(vehicle);
        } else {
            route.setVehicle(null);
        }

        // Coordinates
        if (req.getOriginLatitude() != null && req.getOriginLongitude() != null) {
            route.setOriginLatitude(req.getOriginLatitude());
            route.setOriginLongitude(req.getOriginLongitude());
        }
        if (req.getDestinationLatitude() != null && req.getDestinationLongitude() != null) {
            route.setDestinationLatitude(req.getDestinationLatitude());
            route.setDestinationLongitude(req.getDestinationLongitude());
        }

        route.setUpdatedAt(LocalDateTime.now());
        Route saved = routeRepository.save(route);
        log.info("Admin updated route: {} (ID: {})", saved.getRouteCode(), saved.getId());
        return convertToDTO(saved);
    }

    public AdminRouteDTO updateRouteStatus(Long id, RouteStatus status) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        if (status == RouteStatus.ACTIVE) {
            if (route.getDriver() != null && routeRepository.existsByDriverIdAndStatusAndIdNot(route.getDriver().getId(), RouteStatus.ACTIVE, id)) {
                throw new AssignmentConflictException("Driver is already assigned to another active route.");
            }
            if (route.getVehicle() != null && routeRepository.existsByVehicleIdAndStatusAndIdNot(route.getVehicle().getId(), RouteStatus.ACTIVE, id)) {
                throw new AssignmentConflictException("Vehicle is already assigned to another active route.");
            }
        }
        route.setStatus(status);
        route.setUpdatedAt(LocalDateTime.now());
        Route saved = routeRepository.save(route);
        log.info("Admin updated route status: {} -> {}", saved.getRouteCode(), status);

        try {
            notificationService.notifyAdmins(
                    "Route Status Updated",
                    "Route " + saved.getRouteCode() + " (" + saved.getOrigin() + " to " + saved.getDestination() + ") changed to " + status.name() + ".",
                    "ROUTE_UPDATE"
            );
        } catch (Exception ex) {
            log.warn("Failed to create route notification: {}", ex.getMessage());
        }

        return convertToDTO(saved);
    }

    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        long activeLoads = shipmentRepository.countActiveShipmentsByRoute(route);
        if (activeLoads > 0) {
            throw new IllegalArgumentException("This route cannot be deleted because it has active shipments.");
        }

        // Detach any completed/historical shipments from this route before deleting
        List<Shipment> pastShipments = shipmentRepository.findByRoute(route);
        for (Shipment s : pastShipments) {
            s.setRoute(null);
            shipmentRepository.save(s);
        }

        log.info("Admin deleted route: {}", route.getRouteCode());
        routeRepository.delete(route);
    }

    private AdminRouteDTO convertToDTO(Route route) {
        AdminRouteDTO dto = new AdminRouteDTO();
        dto.setId(route.getId());
        dto.setRouteCode(route.getRouteCode());
        dto.setRouteName(route.getRouteName());
        dto.setOrigin(route.getOrigin());
        dto.setOriginName(route.getOriginName());
        dto.setOriginCity(route.getOriginCity());
        dto.setOriginState(route.getOriginState());
        dto.setDestination(route.getDestination());
        dto.setDestinationName(route.getDestinationName());
        dto.setDestinationCity(route.getDestinationCity());
        dto.setDestinationState(route.getDestinationState());
        dto.setDistance(route.getDistanceKm());
        dto.setEstimatedTransit(route.getEstimatedTransitTimeHours());
        dto.setEstimatedTransitHours(route.getEstimatedTransitTimeHours());
        dto.setOriginAddress(route.getOriginAddress());
        dto.setDestinationAddress(route.getDestinationAddress());
        dto.setDistanceKm(route.getDistanceKm());
        dto.setEstimatedTransitTimeHours(route.getEstimatedTransitTimeHours());
        dto.setStatus(route.getStatus());

        List<Shipment> activeShipments = shipmentRepository.findByRoute(route).stream()
                .filter(s -> s.getStatus() != OrderStatus.DELIVERED && s.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        Driver operationalDriver = route.getDriver();
        Vehicle operationalVehicle = route.getVehicle();

        if (!activeShipments.isEmpty()) {
            Shipment s = activeShipments.get(0);
            if (s.getDriver() != null) {
                operationalDriver = s.getDriver();
            }
            if (s.getVehicle() != null) {
                operationalVehicle = s.getVehicle();
            }
        }

        if (operationalDriver != null) {
            dto.setDriverId(operationalDriver.getId());
            dto.setDriverName(operationalDriver.getFullName());
            dto.setDriverPhone(operationalDriver.getPhone());
            dto.setDriverStatus(operationalDriver.getStatus());
        }

        if (operationalVehicle != null) {
            dto.setVehicleId(operationalVehicle.getId());
            dto.setVehicleNumber(operationalVehicle.getVehicleNumber());
            dto.setVehicleModel(operationalVehicle.getModel());
            dto.setVehicleType(operationalVehicle.getVehicleType());
            dto.setVehicleCapacity(operationalVehicle.getCapacity());
            dto.setVehicleCapacityUnit(operationalVehicle.getCapacityUnit());
        }

        long activeLoads = shipmentRepository.countActiveShipmentsByRoute(route);
        dto.setActiveLoads(activeLoads);
        dto.setTotalShipments(shipmentRepository.countByRoute(route));

        dto.setOriginLatitude(route.getOriginLatitude());
        dto.setOriginLongitude(route.getOriginLongitude());
        dto.setDestinationLatitude(route.getDestinationLatitude());
        dto.setDestinationLongitude(route.getDestinationLongitude());

        for (Shipment s : activeShipments) {
            if (s.getCurrentLatitude() != null && s.getCurrentLongitude() != null) {
                dto.setCurrentShipmentTrackingNumber(s.getTrackingNumber());
                dto.setCurrentShipmentLocation(s.getCurrentLocationName());
                dto.setCurrentShipmentLatitude(s.getCurrentLatitude());
                dto.setCurrentShipmentLongitude(s.getCurrentLongitude());
                break;
            }
        }

        dto.setCreatedAt(route.getCreatedAt());
        dto.setUpdatedAt(route.getUpdatedAt());

        return dto;
    }

    private String generateRouteCode(String origin, String dest) {
        String origCode = origin.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (origCode.length() > 3) origCode = origCode.substring(0, 3);
        String destCode = dest.replaceAll("[^A-Za-z]", "").toUpperCase();
        if (destCode.length() > 3) destCode = destCode.substring(0, 3);
        return "RT-" + origCode + "-" + destCode + "-" + (int)(Math.random() * 900 + 100);
    }

    @Transactional(readOnly = true)
    public List<com.primeroute.logistics.dto.RouteShipmentDTO> getShipmentsByRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        List<Shipment> shipments = shipmentRepository.findByRoute(route);
        return shipments.stream().map(s -> {
            com.primeroute.logistics.dto.RouteShipmentDTO dto = new com.primeroute.logistics.dto.RouteShipmentDTO();
            dto.setId(s.getId());
            dto.setTrackingNumber(s.getTrackingNumber());
            if (s.getOrder() != null) {
                dto.setOrderNumber(s.getOrder().getOrderNumber());
            }
            dto.setStatus(s.getStatus());
            dto.setCurrentLocationName(s.getCurrentLocationName());
            dto.setCurrentLatitude(s.getCurrentLatitude());
            dto.setCurrentLongitude(s.getCurrentLongitude());
            if (s.getEvents() != null && !s.getEvents().isEmpty()) {
                ShipmentTrackingEvent latest = s.getEvents().get(s.getEvents().size() - 1);
                dto.setLatestEventTitle(latest.getTitle());
                dto.setLatestEventLocation(latest.getLocationName());
                dto.setLatestEventTime(latest.getEventTime());
            }
            return dto;
        }).collect(Collectors.toList());
    }
}