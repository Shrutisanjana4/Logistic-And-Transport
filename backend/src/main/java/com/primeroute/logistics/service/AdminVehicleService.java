package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminVehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ShipmentRepository shipmentRepository;
    private final WarehouseRepository warehouseRepository;

    @Autowired
    public AdminVehicleService(
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository,
            ShipmentRepository shipmentRepository,
            WarehouseRepository warehouseRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.shipmentRepository = shipmentRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() != VehicleStatus.INACTIVE && v.getStatus() != VehicleStatus.MAINTENANCE)
                .filter(v -> shipmentRepository.findActiveShipmentsByVehicle(v).isEmpty())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return convertToDTO(vehicle);
    }

    public VehicleDTO createVehicle(CreateVehicleRequest req) {
        String cleanVehicleNumber = req.getVehicleNumber().trim().toUpperCase();
        if (vehicleRepository.existsByVehicleNumber(cleanVehicleNumber)) {
            throw new IllegalArgumentException("Vehicle with number '" + cleanVehicleNumber + "' already exists.");
        }

        String cleanRegNumber = req.getRegistrationNumber().trim().toUpperCase();
        if (vehicleRepository.existsByRegistrationNumber(cleanRegNumber)) {
            throw new IllegalArgumentException("Vehicle with registration plate '" + cleanRegNumber + "' already exists.");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber(cleanVehicleNumber);
        vehicle.setVehicleType(req.getVehicleType());
        vehicle.setManufacturer(req.getManufacturer() != null ? req.getManufacturer().trim() : null);
        vehicle.setModel(req.getModel().trim());
        vehicle.setCapacity(req.getCapacity());
        vehicle.setCapacityUnit(req.getCapacityUnit() != null && !req.getCapacityUnit().isBlank()
                ? req.getCapacityUnit().trim().toUpperCase() : "TONS");
        vehicle.setRegistrationNumber(cleanRegNumber);
        vehicle.setRegistrationExpiry(req.getRegistrationExpiry());
        vehicle.setInsuranceExpiry(req.getInsuranceExpiry());
        vehicle.setCurrentLocation(req.getCurrentLocation() != null ? req.getCurrentLocation().trim() : null);
        vehicle.setStatus(req.getStatus() != null ? req.getStatus() : VehicleStatus.AVAILABLE);

        if (req.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(req.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + req.getWarehouseId()));
            vehicle.setWarehouse(warehouse);
        }

        if (req.getDriverId() != null) {
            Driver driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + req.getDriverId()));
            disassociateDriverFromOtherVehicles(driver, null);
            disassociateDriverFromOtherVehicles(driver, vehicle.getId());
            vehicle.setAssignedDriver(driver);
            if (vehicle.getWarehouse() != null) {
                driver.setWarehouse(vehicle.getWarehouse());
                driverRepository.save(driver);
            }
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        return convertToDTO(saved);
    }

    public VehicleDTO updateVehicle(Long id, UpdateVehicleRequest req) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        String cleanVehicleNumber = req.getVehicleNumber().trim().toUpperCase();
        if (!vehicle.getVehicleNumber().equalsIgnoreCase(cleanVehicleNumber)) {
            if (vehicleRepository.existsByVehicleNumber(cleanVehicleNumber)) {
                throw new IllegalArgumentException("Vehicle number '" + cleanVehicleNumber + "' is already in use.");
            }
            vehicle.setVehicleNumber(cleanVehicleNumber);
        }

        String cleanRegNumber = req.getRegistrationNumber().trim().toUpperCase();
        if (!vehicle.getRegistrationNumber().equalsIgnoreCase(cleanRegNumber)) {
            if (vehicleRepository.existsByRegistrationNumber(cleanRegNumber)) {
                throw new IllegalArgumentException("Registration plate '" + cleanRegNumber + "' is already in use.");
            }
            vehicle.setRegistrationNumber(cleanRegNumber);
        }

        vehicle.setVehicleType(req.getVehicleType());
        vehicle.setManufacturer(req.getManufacturer() != null ? req.getManufacturer().trim() : null);
        vehicle.setModel(req.getModel().trim());
        vehicle.setCapacity(req.getCapacity());
        if (req.getCapacityUnit() != null && !req.getCapacityUnit().isBlank()) {
            vehicle.setCapacityUnit(req.getCapacityUnit().trim().toUpperCase());
        }
        vehicle.setRegistrationExpiry(req.getRegistrationExpiry());
        vehicle.setInsuranceExpiry(req.getInsuranceExpiry());
        vehicle.setCurrentLocation(req.getCurrentLocation() != null ? req.getCurrentLocation().trim() : null);

        if (req.getStatus() != null) {
            vehicle.setStatus(req.getStatus());
        }

        if (req.getWarehouseId() != null) {
            Warehouse newWh = warehouseRepository.findById(req.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + req.getWarehouseId()));
            if (vehicle.getWarehouse() != null && !vehicle.getWarehouse().getId().equals(newWh.getId())) {
                List<Shipment> active = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
                if (!active.isEmpty()) {
                    throw new IllegalArgumentException("Cannot change warehouse for vehicle " + vehicle.getVehicleNumber() + " while it is assigned to active shipment " + active.get(0).getTrackingNumber() + ".");
                }
            }
            vehicle.setWarehouse(newWh);
        } else {
            List<Shipment> active = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
            if (!active.isEmpty()) {
                throw new IllegalArgumentException("Cannot unassign warehouse for vehicle " + vehicle.getVehicleNumber() + " while it is assigned to active shipment " + active.get(0).getTrackingNumber() + ".");
            }
            vehicle.setWarehouse(null);
        }

        if (req.getDriverId() != null) {
            Driver driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + req.getDriverId()));
            disassociateDriverFromOtherVehicles(driver, null);
            disassociateDriverFromOtherVehicles(driver, vehicle.getId());
            vehicle.setAssignedDriver(driver);
            if (vehicle.getWarehouse() != null) {
                driver.setWarehouse(vehicle.getWarehouse());
                driverRepository.save(driver);
            }
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        return convertToDTO(saved);
    }

    private void disassociateDriverFromOtherVehicles(Driver driver, Long currentVehicleId) {
        if (driver == null) return;
        List<Vehicle> existingVehicles = vehicleRepository.findByAssignedDriver(driver);
        for (Vehicle other : existingVehicles) {
            if (currentVehicleId == null || !other.getId().equals(currentVehicleId)) {
                other.setAssignedDriver(null);
                if (other.getStatus() == VehicleStatus.ASSIGNED) {
                    other.setStatus(VehicleStatus.AVAILABLE);
                }
                vehicleRepository.save(other);
            }
        }
    }

    public VehicleDTO assignDriver(Long id, AssignDriverRequest req) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE || vehicle.getStatus() == VehicleStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign driver to vehicle while its status is " + vehicle.getStatus() + ". Set vehicle to AVAILABLE first.");
        }

        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByVehicle(vehicle);

        if (req.getDriverId() == null) {
            vehicle.setAssignedDriver(null);
            if (activeShipments.isEmpty() && vehicle.getStatus() == VehicleStatus.ASSIGNED) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            if (!activeShipments.isEmpty()) {
                Shipment s = activeShipments.get(0);
                Driver prevDriver = s.getDriver();
                s.setDriver(null);
                s.setUpdatedAt(LocalDateTime.now());
                shipmentRepository.save(s);
                if (prevDriver != null) {
                    List<Shipment> remaining = shipmentRepository.findActiveShipmentsByDriver(prevDriver);
                    if (remaining.isEmpty() && prevDriver.getStatus() != DriverStatus.INACTIVE) {
                        prevDriver.setStatus(DriverStatus.AVAILABLE);
                        driverRepository.save(prevDriver);
                    }
                }
            }
        } else {
            Driver driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + req.getDriverId()));

            if (driver.getStatus() == DriverStatus.INACTIVE || driver.getStatus() == DriverStatus.OFF_DUTY) {
                throw new IllegalArgumentException("Cannot assign driver " + driver.getFullName() + " because driver status is " + driver.getStatus() + ".");
            }

            disassociateDriverFromOtherVehicles(driver, vehicle.getId());
            vehicle.setAssignedDriver(driver);
            if (vehicle.getWarehouse() != null) {
                driver.setWarehouse(vehicle.getWarehouse());
                driverRepository.save(driver);
            }
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE && activeShipments.isEmpty()) {
                vehicle.setStatus(VehicleStatus.ASSIGNED);
            }

            if (!activeShipments.isEmpty()) {
                Shipment s = activeShipments.get(0);
                s.setDriver(driver);
                s.setUpdatedAt(LocalDateTime.now());
                shipmentRepository.save(s);
                driver.setStatus(DriverStatus.ON_TRIP);
                driverRepository.save(driver);
            }
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        return convertToDTO(saved);
    }

    public VehicleDTO updateStatus(Long id, UpdateVehicleStatusRequest req) {
        return updateVehicleStatus(id, req);
    }

    public VehicleDTO updateVehicleStatus(Long id, UpdateVehicleStatusRequest req) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        VehicleStatus newStatus = req.getStatus();
        VehicleStatus currentStatus = vehicle.getStatus();

        if (currentStatus == newStatus) {
            return convertToDTO(vehicle);
        }

        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByVehicle(vehicle);

        if ((newStatus == VehicleStatus.INACTIVE || newStatus == VehicleStatus.MAINTENANCE) && !activeShipments.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot set vehicle status to " + newStatus + " while it is assigned to active shipment '"
                    + activeShipments.get(0).getTrackingNumber() + "'. Reassign or complete shipment first."
            );
        }

        vehicle.setStatus(newStatus);
        Vehicle saved = vehicleRepository.save(vehicle);
        return convertToDTO(saved);
    }

    public void deactivateVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
        if (!activeShipments.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cannot deactivate vehicle '" + vehicle.getVehicleNumber() + "' while it has an active shipment: "
                    + activeShipments.get(0).getTrackingNumber() + "."
            );
        }

        vehicle.setStatus(VehicleStatus.INACTIVE);
        vehicleRepository.save(vehicle);
    }

    private VehicleDTO convertToDTO(Vehicle v) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(v.getId());
        dto.setVehicleNumber(v.getVehicleNumber());
        dto.setVehicleType(v.getVehicleType());
        dto.setManufacturer(v.getManufacturer());
        dto.setModel(v.getModel());
        dto.setCapacity(v.getCapacity());
        dto.setCapacityUnit(v.getCapacityUnit());
        dto.setRegistrationNumber(v.getRegistrationNumber());
        dto.setRegistrationExpiry(v.getRegistrationExpiry());
        dto.setInsuranceExpiry(v.getInsuranceExpiry());
        dto.setStatus(v.getStatus());
        dto.setCurrentLocation(v.getCurrentLocation());
        dto.setCreatedAt(v.getCreatedAt());
        dto.setUpdatedAt(v.getUpdatedAt());

        if (v.getWarehouse() != null) {
            dto.setWarehouseId(v.getWarehouse().getId());
            dto.setWarehouseName(v.getWarehouse().getWarehouseName());
        }

        if (v.getAssignedDriver() != null) {
            dto.setDriverId(v.getAssignedDriver().getId());
            dto.setDriverName(v.getAssignedDriver().getFullName());
            dto.setDriverPhone(v.getAssignedDriver().getPhone());
        } else {
            dto.setDriverName("Not Assigned");
        }

        List<Shipment> activeShipments = shipmentRepository.findActiveShipmentsByVehicle(v);
        if (!activeShipments.isEmpty()) {
            Shipment s = activeShipments.get(0);
            dto.setCurrentShipmentId(s.getId());
            dto.setCurrentShipmentTrackingNumber(s.getTrackingNumber());
            dto.setCurrentShipmentStatus(s.getStatus() != null ? s.getStatus().name() : null);
            if (s.getOrder() != null) {
                dto.setCurrentShipmentOrderNumber(s.getOrder().getOrderNumber());
            }
            if (s.getOriginWarehouse() != null) {
                dto.setCurrentShipmentOrigin(s.getOriginWarehouse().getWarehouseName());
            }
            if (s.getDestinationWarehouse() != null) {
                dto.setCurrentShipmentDestination(s.getDestinationWarehouse().getWarehouseName());
            }
        } else {
            dto.setCurrentShipmentTrackingNumber("No active shipment");
        }

        return dto;
    }
}