package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.warehouse.*;
import com.primeroute.logistics.dto.driver.ProofOfDeliveryDTO;
import com.primeroute.logistics.repository.ProofOfDeliveryRepository;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseManagerService {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ShipmentRepository shipmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final ShipmentTrackingEventRepository trackingEventRepository;
    private final OrderRepository orderRepository;
    private final ShipmentService shipmentService;
    private final ProofOfDeliveryRepository proofOfDeliveryRepository;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    public WarehouseManagerService(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository,
            InventoryRepository inventoryRepository,
            InventoryTransactionRepository transactionRepository,
            ShipmentRepository shipmentRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            ShipmentTrackingEventRepository trackingEventRepository,
            OrderRepository orderRepository,
            ShipmentService shipmentService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) ProofOfDeliveryRepository proofOfDeliveryRepository) {
        this.userRepository = userRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.shipmentRepository = shipmentRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.orderRepository = orderRepository;
        this.shipmentService = shipmentService;
        this.proofOfDeliveryRepository = proofOfDeliveryRepository;
    }

    public WarehouseManagerService(
            UserRepository userRepository,
            WarehouseRepository warehouseRepository,
            InventoryRepository inventoryRepository,
            InventoryTransactionRepository transactionRepository,
            ShipmentRepository shipmentRepository,
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            ShipmentTrackingEventRepository trackingEventRepository,
            OrderRepository orderRepository,
            ShipmentService shipmentService) {
        this(userRepository, warehouseRepository, inventoryRepository, transactionRepository, shipmentRepository, driverRepository, vehicleRepository, trackingEventRepository, orderRepository, shipmentService, null);
    }

    /**
     * Resolves the authenticated user and their scoped Warehouse.
     * Enforces strict authorization: Warehouse Managers cannot access data outside their assigned warehouse.
     */
    public Warehouse getScopedWarehouse() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required.");
        }

        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
                .or(() -> userRepository.findByEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        if (user.getWarehouse() != null) {
            return user.getWarehouse();
        }

        // For ADMIN inspecting warehouse manager portal, fallback to first active warehouse
        if (user.getRole() == Role.ADMIN) {
            List<Warehouse> active = warehouseRepository.findByStatus(WarehouseStatus.ACTIVE);
            if (!active.isEmpty()) {
                return active.get(0);
            }
            return warehouseRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No warehouses configured in the system."));
        }

        throw new AccessDeniedException("No warehouse assigned to account: " + email + ". Please contact your administrator.");
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : "system";
        return userRepository.findByEmailIgnoreCase(email)
                .or(() -> userRepository.findByEmail(email))
                .orElse(null);
    }

    // ==========================================
    // PROFILE & WAREHOUSE DETAILS
    // ==========================================

    @Transactional(readOnly = true)
    public Map<String, Object> getManagerProfile() {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();

        Map<String, Object> profile = new HashMap<>();
        profile.put("email", user != null ? user.getEmail() : "");
        profile.put("fullName", user != null ? user.getFullName() : "");
        profile.put("role", user != null ? user.getRole().name() : Role.WAREHOUSE_MANAGER.name());
        profile.put("phone", user != null ? user.getPhone() : "");
        profile.put("warehouseId", warehouse.getId());
        profile.put("warehouseCode", warehouse.getWarehouseCode());
        profile.put("warehouseName", warehouse.getWarehouseName());
        profile.put("warehouseCity", warehouse.getCity());
        profile.put("warehouseState", warehouse.getState());
        profile.put("warehouseAddress", warehouse.getAddressLine1());
        profile.put("operatingHours", warehouse.getOperatingHours());
        profile.put("warehouseCapacity", warehouse.getTotalCapacity());
        profile.put("capacityUnit", warehouse.getCapacityUnit());
        return profile;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWarehouseDetails() {
        Warehouse w = getScopedWarehouse();
        Map<String, Object> m = new HashMap<>();
        m.put("id", w.getId());
        m.put("code", w.getWarehouseCode());
        m.put("name", w.getWarehouseName());
        m.put("address", w.getAddressLine1() + (w.getAddressLine2() != null ? ", " + w.getAddressLine2() : ""));
        m.put("city", w.getCity());
        m.put("state", w.getState());
        m.put("postalCode", w.getPostalCode());
        m.put("latitude", w.getLatitude() != null ? w.getLatitude() : 12.9716);
        m.put("longitude", w.getLongitude() != null ? w.getLongitude() : 77.5946);
        m.put("totalCapacity", w.getTotalCapacity() != null ? w.getTotalCapacity() : 10000.0);
        m.put("currentOccupancy", w.getOccupiedCapacity() != null ? w.getOccupiedCapacity() : 0.0);
        m.put("operatingHours", w.getOperatingHours());
        m.put("contactPhone", w.getContactPhone());
        User user = getAuthenticatedUser();
        m.put("managerName", user != null && user.getFullName() != null ? user.getFullName() : w.getManagerName());
        m.put("managerEmail", user != null && user.getEmail() != null ? user.getEmail() : w.getManagerEmail());
        m.put("dockDoors", 12);
        m.put("warehouseType", w.getWarehouseType() != null ? w.getWarehouseType().name() : "DISTRIBUTION_CENTER");
        m.put("status", w.getStatus() != null ? w.getStatus().name() : "ACTIVE");
        m.put("country", w.getCountry() != null ? w.getCountry() : "India");
        m.put("capacityUnit", w.getCapacityUnit() != null ? w.getCapacityUnit() : "KG");
        m.put("contactEmail", w.getContactEmail() != null ? w.getContactEmail() : "");
        m.put("managerPhone", w.getManagerPhone() != null ? w.getManagerPhone() : "");
        return m;
    }

    public Warehouse updateWarehouseDetails(UpdateWarehouseDetailsRequest request) {
        Warehouse warehouse = getScopedWarehouse();
        if (request.getOperatingHours() != null && !request.getOperatingHours().isBlank()) {
            warehouse.setOperatingHours(request.getOperatingHours().trim());
        }
        if (request.getContactPhone() != null && !request.getContactPhone().isBlank()) {
            warehouse.setContactPhone(request.getContactPhone().trim());
        }
        if (request.getContactEmail() != null && !request.getContactEmail().isBlank()) {
            warehouse.setContactEmail(request.getContactEmail().trim());
        }
        if (request.getManagerPhone() != null && !request.getManagerPhone().isBlank()) {
            warehouse.setManagerPhone(request.getManagerPhone().trim());
        }
        return warehouseRepository.save(warehouse);
    }

    // ==========================================
    // DASHBOARD
    // ==========================================

    @Transactional(readOnly = true)
    public WarehouseDashboardDTO getDashboard() {
        Warehouse warehouse = getScopedWarehouse();
        WarehouseDashboardDTO dto = new WarehouseDashboardDTO();

        // Warehouse Info
        dto.setWarehouseId(warehouse.getId());
        dto.setWarehouseCode(warehouse.getWarehouseCode());
        dto.setWarehouseName(warehouse.getWarehouseName());
        dto.setWarehouseType(warehouse.getWarehouseType().name());
        User user = getAuthenticatedUser();
        String effectiveManagerName = (user != null && user.getFullName() != null && !user.getFullName().isBlank())
                ? user.getFullName()
                : (warehouse.getManagerName() != null ? warehouse.getManagerName() : "Warehouse Manager");
        String effectiveManagerEmail = (user != null && user.getEmail() != null && !user.getEmail().isBlank())
                ? user.getEmail()
                : (warehouse.getManagerEmail() != null ? warehouse.getManagerEmail() : "");
        dto.setManagerName(effectiveManagerName);
        dto.setManagerEmail(effectiveManagerEmail);
        dto.setCity(warehouse.getCity());
        dto.setState(warehouse.getState());
        dto.setTotalCapacity(warehouse.getTotalCapacity());
        dto.setOccupiedCapacity(warehouse.getOccupiedCapacity());
        dto.setAvailableCapacity(Math.max(0.0, (warehouse.getTotalCapacity() != null ? warehouse.getTotalCapacity() : 0.0) - (warehouse.getOccupiedCapacity() != null ? warehouse.getOccupiedCapacity() : 0.0)));
        dto.setUtilizationRate(warehouse.getTotalCapacity() != null && warehouse.getTotalCapacity() > 0 ? (warehouse.getOccupiedCapacity() / warehouse.getTotalCapacity()) * 100 : 0.0);
        dto.setStatus(warehouse.getStatus().name());
        dto.setOperatingHours(warehouse.getOperatingHours());

        // Inventory KPIs
        dto.setTotalInventoryItems(inventoryRepository.countByWarehouse(warehouse));
        dto.setAvailableStock(inventoryRepository.sumAvailableStockByWarehouse(warehouse));
        dto.setReservedStock(inventoryRepository.sumReservedStockByWarehouse(warehouse));
        dto.setDamagedStock(inventoryRepository.sumDamagedStockByWarehouse(warehouse));

        List<Inventory> lowStockList = inventoryRepository.findLowStockByWarehouse(warehouse);
        dto.setLowStockCount(lowStockList.size());
        dto.setLowStockAlerts(lowStockList.stream().limit(5).map(this::mapToInventoryDTO).collect(Collectors.toList()));

        // Shipment KPIs
        // Direct Delivery: Zero incoming shipments
        dto.setIncomingShipmentsCount(0);
        dto.setIncomingPreview(Collections.emptyList());

        List<Shipment> originShipments = shipmentRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse);
        List<Shipment> nonCancelledOutgoing = originShipments.stream()
                .filter(s -> s.getStatus() != OrderStatus.CANCELLED)
                .collect(Collectors.toList());
        dto.setOutgoingShipmentsCount(nonCancelledOutgoing.size());
        dto.setOutgoingPreview(nonCancelledOutgoing.stream().limit(5).map(this::mapToShipmentDTO).collect(Collectors.toList()));

        // Active warehouse operational queue stage counts (scoped to warehouse)
        long readyToProcessCount = 0;
        long pickingCount = 0;
        long packedCount = 0;
        long readyForDispatchCount = 0;

        for (Shipment s : originShipments) {
            // Exclude terminal / in-transit / delivered / cancelled shipments from active warehouse processing queue
            if (s.getStatus() == OrderStatus.DELIVERED || s.getStatus() == OrderStatus.CANCELLED
                    || s.getStatus() == OrderStatus.IN_TRANSIT || s.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
                continue;
            }
            String stage = s.getOperationalStage() != null ? s.getOperationalStage().trim().toUpperCase() : "READY_FOR_PROCESSING";
            if ("DISPATCHED".equals(stage) || "IN_TRANSIT".equals(stage) || "OUT_FOR_DELIVERY".equals(stage)
                    || "DELIVERED".equals(stage) || "CANCELLED".equals(stage)) {
                continue;
            }

            if ("READY_TO_PROCESS".equals(stage) || "READY_FOR_PROCESSING".equals(stage)) {
                readyToProcessCount++;
            } else if ("PICKING".equals(stage)) {
                pickingCount++;
            } else if ("PACKED".equals(stage)) {
                packedCount++;
            } else if ("READY_FOR_DISPATCH".equals(stage)) {
                readyForDispatchCount++;
            }
        }

        dto.setReadyToProcessCount(readyToProcessCount);
        dto.setPickingCount(pickingCount);
        dto.setPackedCount(packedCount);
        dto.setReadyForDispatchCount(readyForDispatchCount);

        // Fleet KPIs (scoped to warehouse)
        List<Driver> warehouseDrivers = driverRepository.findAll().stream()
                .filter(d -> d.getWarehouse() != null && d.getWarehouse().getId().equals(warehouse.getId()))
                .collect(Collectors.toList());
        long availDrivers = warehouseDrivers.stream().filter(d -> d.getStatus() == DriverStatus.AVAILABLE).count();
        dto.setDriversAvailableCount(availDrivers);
        dto.setDriversTotalCount(warehouseDrivers.size());

        List<Vehicle> warehouseVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getWarehouse() != null && v.getWarehouse().getId().equals(warehouse.getId()))
                .collect(Collectors.toList());
        long availVehicles = warehouseVehicles.stream().filter(v -> v.getStatus() == VehicleStatus.AVAILABLE).count();
        dto.setVehiclesAvailableCount(availVehicles);
        dto.setVehiclesTotalCount(warehouseVehicles.size());

        // Recent Activity
        List<InventoryTransaction> transactions = transactionRepository.findTop20ByWarehouseOrderByTimestampDesc(warehouse);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
        List<WarehouseActivityDTO> activities = transactions.stream().limit(8).map(t -> {
            String what = t.getTransactionType().name() + " " + t.getQuantity() + " units of " + t.getSku() + " (" + t.getProductName() + ")";
            String when = t.getTimestamp() != null ? t.getTimestamp().format(dtf) : "Recently";
            return new WarehouseActivityDTO(
                    t.getPerformedBy(),
                    what,
                    warehouse.getWarehouseName(),
                    when,
                    t.getTimestamp(),
                    t.getTransactionType().name(),
                    t.getReferenceShipment() != null ? t.getReferenceShipment() : t.getReferenceOrder()
            );
        }).collect(Collectors.toList());

        dto.setRecentActivities(activities);

        // Compute Fulfillment Alerts (Requirement 36)
        Map<Long, FulfillmentAlertDTO> alertMap = new LinkedHashMap<>();
        for (Shipment s : nonCancelledOutgoing) {
            if (s.getOrder() != null && s.getOrder().getItems() != null) {
                for (OrderItem item : s.getOrder().getItems()) {
                    if (item.getProduct() != null) {
                        Product p = item.getProduct();
                        alertMap.compute(p.getId(), (k, existing) -> {
                            long newReq = (existing == null ? 0L : existing.getRequiredQuantity()) + item.getQuantity();
                            long ordersCount = (existing == null ? 0L : existing.getPendingOrdersCount()) + 1L;
                            Optional<Inventory> invOpt = inventoryRepository.findByWarehouseAndProduct(warehouse, p);
                            int avail = invOpt.map(Inventory::getAvailableStock).orElse(0);
                            String status = avail >= newReq ? "AVAILABLE" : "INSUFFICIENT_STOCK";
                            return new FulfillmentAlertDTO(p.getId(), p.getSku(), p.getName(), p.getCategory(), newReq, avail, ordersCount, status);
                        });
                    }
                }
            }
        }
        dto.setFulfillmentAlerts(new ArrayList<>(alertMap.values()));

        return dto;
    }

    // ==========================================
    // INVENTORY MANAGEMENT
    // ==========================================

    @Transactional(readOnly = true)
    public List<WarehouseInventoryDTO> getInventory(String search, String category, String status) {
        Warehouse warehouse = getScopedWarehouse();
        List<Inventory> items = inventoryRepository.findByWarehouseOrderBySkuAsc(warehouse);

        return items.stream()
                .filter(i -> {
                    if (search != null && !search.isBlank()) {
                        String s = search.trim().toLowerCase();
                        boolean matchSku = i.getSku() != null && i.getSku().toLowerCase().contains(s);
                        boolean matchName = i.getProductName() != null && i.getProductName().toLowerCase().contains(s);
                        if (!matchSku && !matchName) return false;
                    }
                    if (category != null && !category.isBlank() && !category.equalsIgnoreCase("ALL")) {
                        if (!category.equalsIgnoreCase(i.getCategory())) return false;
                    }
                    if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
                        if (!status.equalsIgnoreCase(i.getStatus())) return false;
                    }
                    return true;
                })
                .map(this::mapToInventoryDTO)
                .collect(Collectors.toList());
    }

    public WarehouseInventoryDTO receiveStock(ReceiveStockRequest request) {
        User user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new AccessDeniedException("Only Warehouse Managers can perform standard operational fleet assignments. Administrators must use the Admin Override Assignment API.");
        }
        Warehouse warehouse = getScopedWarehouse();
        String performer = "Warehouse Manager (" + user.getFullName() + ")";

        Inventory inventory = inventoryRepository.findByWarehouseAndSku(warehouse, request.getSku())
                .orElseGet(() -> {
                    Inventory newItem = new Inventory();
                    newItem.setWarehouse(warehouse);
                    newItem.setSku(request.getSku().trim().toUpperCase());
                    newItem.setProductName(request.getProductName() != null ? request.getProductName().trim() : "Product " + request.getSku());
                    newItem.setCategory(request.getCategory() != null ? request.getCategory().trim() : "General Cargo");
                    newItem.setTotalStock(0);
                    newItem.setReservedStock(0);
                    newItem.setDamagedStock(0);
                    newItem.setReorderLevel(request.getReorderLevel() != null ? request.getReorderLevel() : 10);
                    newItem.setUnitPrice(request.getUnitPrice() != null ? request.getUnitPrice() : 0.0);
                    newItem.setUnit(request.getUnit() != null ? request.getUnit() : "Units");
                    return newItem;
                });

        int prevAvailable = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
        inventory.setTotalStock((inventory.getTotalStock() != null ? inventory.getTotalStock() : 0) + request.getQuantity());
        inventory.recalculate();
        Inventory saved = inventoryRepository.save(inventory);

        // Record Audit Transaction
        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(saved);
        tx.setWarehouse(warehouse);
        tx.setSku(saved.getSku());
        tx.setProductName(saved.getProductName());
        tx.setTransactionType(InventoryTransactionType.RECEIVE);
        tx.setQuantity(request.getQuantity());
        tx.setPreviousAvailable(prevAvailable);
        tx.setNewAvailable(saved.getAvailableStock());
        tx.setPerformedBy(performer);
        tx.setReason(request.getReason() != null ? request.getReason() : "Stock Received Inbound");
        tx.setReferenceOrder(request.getReferenceNumber());
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        return mapToInventoryDTO(saved);
    }

    public WarehouseInventoryDTO reserveStock(ReserveStockRequest request) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Inventory inventory = inventoryRepository.findByWarehouseAndSku(warehouse, request.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found in this warehouse: " + request.getSku()));

        int prevAvailable = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
        if (prevAvailable < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient available stock to reserve. Available: " + prevAvailable + ", Requested: " + request.getQuantity());
        }

        inventory.setReservedStock((inventory.getReservedStock() != null ? inventory.getReservedStock() : 0) + request.getQuantity());
        inventory.recalculate();
        Inventory saved = inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(saved);
        tx.setWarehouse(warehouse);
        tx.setSku(saved.getSku());
        tx.setProductName(saved.getProductName());
        tx.setTransactionType(InventoryTransactionType.RESERVE);
        tx.setQuantity(request.getQuantity());
        tx.setPreviousAvailable(prevAvailable);
        tx.setNewAvailable(saved.getAvailableStock());
        tx.setPerformedBy(performer);
        tx.setReason(request.getReason() != null ? request.getReason() : "Stock Reserved for Outbound Dispatch");
        tx.setReferenceOrder(request.getReferenceOrder());
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        return mapToInventoryDTO(saved);
    }

    public WarehouseInventoryDTO adjustStock(AdjustStockRequest request) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Inventory inventory = inventoryRepository.findByWarehouseAndSku(warehouse, request.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("SKU not found in this warehouse: " + request.getSku()));

        int prevAvailable = inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0;
        int diff = request.getNewTotalStock() - (inventory.getTotalStock() != null ? inventory.getTotalStock() : 0);

        inventory.setTotalStock(request.getNewTotalStock());
        inventory.recalculate();
        Inventory saved = inventoryRepository.save(inventory);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setInventory(saved);
        tx.setWarehouse(warehouse);
        tx.setSku(saved.getSku());
        tx.setProductName(saved.getProductName());
        tx.setTransactionType(InventoryTransactionType.ADJUSTMENT);
        tx.setQuantity(Math.abs(diff));
        tx.setPreviousAvailable(prevAvailable);
        tx.setNewAvailable(saved.getAvailableStock());
        tx.setPerformedBy(performer);
        tx.setReason(request.getReason() + " (Diff: " + (diff >= 0 ? "+" + diff : diff) + ")");
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);

        return mapToInventoryDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<WarehouseTransactionDTO> getTransactions(String sku, LocalDateTime startDate, LocalDateTime endDate) {
        Warehouse warehouse = getScopedWarehouse();
        List<InventoryTransaction> list = transactionRepository.findByWarehouseOrderByTimestampDesc(warehouse);

        if (sku != null && !sku.isBlank()) {
            String s = sku.trim().toLowerCase();
            list = list.stream().filter(t -> t.getSku() != null && t.getSku().toLowerCase().contains(s)).collect(Collectors.toList());
        }
        if (startDate != null) {
            list = list.stream().filter(t -> t.getTimestamp() != null && !t.getTimestamp().isBefore(startDate)).collect(Collectors.toList());
        }
        if (endDate != null) {
            list = list.stream().filter(t -> t.getTimestamp() != null && !t.getTimestamp().isAfter(endDate)).collect(Collectors.toList());
        }

        return list.stream().map(t -> {
            WarehouseTransactionDTO dto = new WarehouseTransactionDTO();
            dto.setId(t.getId());
            dto.setSku(t.getSku());
            dto.setProductName(t.getProductName());
            dto.setTransactionType(t.getTransactionType().name());
            dto.setQuantity(t.getQuantity());
            dto.setPreviousAvailable(t.getPreviousAvailable());
            dto.setNewAvailable(t.getNewAvailable());
            dto.setPerformedBy(t.getPerformedBy());
            dto.setTimestamp(t.getTimestamp());
            dto.setReason(t.getReason());
            dto.setReferenceOrder(t.getReferenceOrder());
            dto.setReferenceShipment(t.getReferenceShipment());
            return dto;
        }).collect(Collectors.toList());
    }

    // ==========================================
    // SHIPMENT OPERATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public List<WarehouseShipmentDTO> getIncomingShipments() {
        // Direct delivery model: No incoming shipments to destination warehouse
        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<WarehouseShipmentDTO> getAllWarehouseShipments() {
        Warehouse warehouse = getScopedWarehouse();
        List<Shipment> list = shipmentRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse);
        return list.stream().map(this::mapToShipmentDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseShipmentDTO> getOutgoingShipments() {
        Warehouse warehouse = getScopedWarehouse();
        List<Shipment> list = shipmentRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse);
        return list.stream()
                .filter(s -> s.getStatus() != OrderStatus.CANCELLED)
                .map(this::mapToShipmentDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WarehouseShipmentDTO> getDispatchReadyShipments() {
        Warehouse warehouse = getScopedWarehouse();
        List<Shipment> list = shipmentRepository.findReadyForDispatchByWarehouse(warehouse);
        return list.stream().map(this::mapToShipmentDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseShipmentDTO getShipmentById(Long id) {
        Warehouse warehouse = getScopedWarehouse();
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));

        boolean isOrigin = shipment.getOriginWarehouse() != null && shipment.getOriginWarehouse().getId().equals(warehouse.getId());
        if (!isOrigin) {
            throw new AccessDeniedException("Unauthorized: Shipment does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        return mapToShipmentDTO(shipment);
    }

    public WarehouseShipmentDTO startPicking(Long id) {
        return updateShipmentStage(id, "PICKING");
    }

    public WarehouseShipmentDTO markPacked(Long id) {
        return updateShipmentStage(id, "PACKED");
    }

    public WarehouseShipmentDTO markReadyForDispatch(Long id) {
        return updateShipmentStage(id, "READY_FOR_DISPATCH");
    }

    public WarehouseShipmentDTO updateShipmentStage(Long id, String stage) {
        if (stage == null || stage.isBlank()) {
            throw new IllegalArgumentException("Operational stage cannot be blank.");
        }
        String targetStage = stage.trim().toUpperCase();

        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));

        // 1. Warehouse scoping validation: Manager can only operate on their own warehouse's shipments
        if (shipment.getOriginWarehouse() == null || !shipment.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Shipment does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        // 2. Terminal status validation: Cancelled or Delivered shipments cannot transition
        if (shipment.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update operational stage for a cancelled shipment.");
        }
        if (shipment.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update operational stage for a delivered shipment.");
        }

        String currentStage = shipment.getOperationalStage() != null ? shipment.getOperationalStage().trim().toUpperCase() : "READY_FOR_PROCESSING";

        // 3. Stage transition state-machine validation
        if ("PICKING".equals(targetStage)) {
            if (!"READY_FOR_PROCESSING".equals(currentStage) && !"READY_TO_PROCESS".equals(currentStage)) {
                throw new IllegalStateException("Invalid stage transition from " + currentStage + " to PICKING. Shipment must be in READY_FOR_PROCESSING stage.");
            }
        } else if ("PACKED".equals(targetStage)) {
            if (!"PICKING".equals(currentStage)) {
                throw new IllegalStateException("Invalid stage transition from " + currentStage + " to PACKED. Shipment must be in PICKING stage.");
            }
        } else if ("READY_FOR_DISPATCH".equals(targetStage)) {
            if (!"PACKED".equals(currentStage)) {
                throw new IllegalStateException("Invalid stage transition from " + currentStage + " to READY_FOR_DISPATCH. Shipment must be in PACKED stage.");
            }
        } else if ("READY_FOR_PROCESSING".equals(targetStage) || "READY_TO_PROCESS".equals(targetStage)) {
            if (!"READY_FOR_PROCESSING".equals(currentStage) && !"READY_TO_PROCESS".equals(currentStage)) {
                throw new IllegalStateException("Cannot revert operational stage back to READY_FOR_PROCESSING from " + currentStage + ".");
            }
        }

        shipment.setOperationalStage(targetStage);
        shipment.setUpdatedAt(LocalDateTime.now());

        // 4. Create and persist tracking milestone event
        String eventTitle;
        String eventDescription;
        if ("PICKING".equals(targetStage)) {
            eventTitle = "Warehouse Picking Started";
            eventDescription = "Warehouse picking started.";
        } else if ("PACKED".equals(targetStage)) {
            eventTitle = "Shipment Packed";
            eventDescription = "Shipment packed and ready for dispatch assignment.";
        } else {
            eventTitle = "Warehouse Stage Updated: " + targetStage;
            eventDescription = "Operational stage updated to " + targetStage + " by " + performer;
        }

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus() != null ? shipment.getStatus() : OrderStatus.CONFIRMED,
                eventTitle,
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                eventDescription
        );
        trackingEventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);
        return mapToShipmentDTO(saved);
    }

    public WarehouseShipmentDTO assignFleet(Long id, AssignFleetRequest request) {
        User user = getAuthenticatedUser();
        if (user == null || user.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new AccessDeniedException("Only Warehouse Managers can perform standard operational fleet assignments. Administrators must use the Admin Override Assignment API.");
        }
        Warehouse warehouse = getScopedWarehouse();
        String performer = "Warehouse Manager (" + user.getFullName() + ")";

        if (request == null || request.getDriverId() == null || request.getVehicleId() == null) {
            throw new IllegalArgumentException("Both Driver and Vehicle must be selected for fleet assignment.");
        }

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));

        if (shipment.getOriginWarehouse() == null || !shipment.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Driver and vehicle assignments are restricted to the origin warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        if (shipment.getStatus() == OrderStatus.CANCELLED || "CANCELLED".equalsIgnoreCase(shipment.getOperationalStage())) {
            throw new IllegalStateException("Cannot assign fleet to a cancelled shipment.");
        }

        if (shipment.getStatus() == OrderStatus.DELIVERED || "DELIVERED".equalsIgnoreCase(shipment.getOperationalStage())) {
            throw new IllegalStateException("Cannot assign fleet to a delivered shipment.");
        }

        String currentStage = shipment.getOperationalStage() != null ? shipment.getOperationalStage().trim().toUpperCase() : "READY_FOR_PROCESSING";
        if (!"PACKED".equals(currentStage) && !"READY_FOR_DISPATCH".equals(currentStage)) {
            throw new IllegalStateException("Shipment must be in PACKED stage before fleet assignment. Current stage: " + currentStage);
        }

        // Driver Validation & Assignment
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + request.getDriverId()));

        if (driver.getWarehouse() == null || !driver.getWarehouse().getId().equals(warehouse.getId())) {
            throw new IllegalArgumentException("Driver " + driver.getFullName() + " does not belong to warehouse " + warehouse.getWarehouseCode() + ".");
        }

        if (driver.getStatus() == DriverStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive driver: " + driver.getFullName() + ".");
        }
        if (driver.getStatus() == DriverStatus.OFF_DUTY) {
            throw new IllegalArgumentException("Cannot assign off-duty driver: " + driver.getFullName() + ".");
        }
        if (driver.getStatus() == DriverStatus.ON_TRIP) {
            List<Shipment> activeOnDriver = shipmentRepository.findActiveShipmentsByDriver(driver);
            String otherTrk = !activeOnDriver.isEmpty() ? activeOnDriver.get(0).getTrackingNumber() : "another trip";
            throw new IllegalArgumentException("Driver " + driver.getFullName() + " is currently on trip with shipment " + otherTrk + ".");
        }

        List<Shipment> activeOnDriver = shipmentRepository.findActiveShipmentsByDriver(driver);
        boolean driverHasOther = activeOnDriver.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
        if (driverHasOther) {
            Shipment other = activeOnDriver.stream().filter(s -> !s.getId().equals(shipment.getId())).findFirst().get();
            throw new IllegalArgumentException("Driver " + driver.getFullName() + " is currently on trip with shipment " + other.getTrackingNumber() + ".");
        }

        // Vehicle Validation & Assignment
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + request.getVehicleId()));

        if (vehicle.getWarehouse() == null || !vehicle.getWarehouse().getId().equals(warehouse.getId())) {
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " does not belong to warehouse " + warehouse.getWarehouseCode() + ".");
        }

        if (vehicle.getStatus() == VehicleStatus.INACTIVE) {
            throw new IllegalArgumentException("Cannot assign inactive vehicle: " + vehicle.getVehicleNumber() + ".");
        }
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new IllegalArgumentException("Cannot assign vehicle undergoing maintenance: " + vehicle.getVehicleNumber() + ".");
        }
        if (vehicle.getStatus() == VehicleStatus.IN_TRANSIT) {
            List<Shipment> activeOnVeh = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
            String otherTrk = !activeOnVeh.isEmpty() ? activeOnVeh.get(0).getTrackingNumber() : "another trip";
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is currently in transit with shipment " + otherTrk + ".");
        }

        if (vehicle.getAssignedDriver() != null && !vehicle.getAssignedDriver().getId().equals(driver.getId())) {
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is configured for driver " + vehicle.getAssignedDriver().getFullName() + " and cannot be assigned to another driver.");
        }

        List<Shipment> activeOnVeh = shipmentRepository.findActiveShipmentsByVehicle(vehicle);
        boolean vehHasOther = activeOnVeh.stream().anyMatch(s -> !s.getId().equals(shipment.getId()));
        if (vehHasOther) {
            Shipment other = activeOnVeh.stream().filter(s -> !s.getId().equals(shipment.getId())).findFirst().get();
            throw new IllegalArgumentException("Vehicle " + vehicle.getVehicleNumber() + " is currently assigned to active shipment " + other.getTrackingNumber() + ".");
        }

        // Atomic assignment to shipment
        shipment.setDriver(driver);
        shipment.setVehicle(vehicle);
        shipment.setOperationalStage("READY_FOR_DISPATCH");
        shipment.setAssignmentStatus("ASSIGNED");
        shipment.setAssignedBy(performer);
        shipment.setAssignedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        // Update Driver & Vehicle Status
        driver.setStatus(DriverStatus.ON_DUTY);
        driverRepository.save(driver);

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicleRepository.save(vehicle);

        // Record tracking milestone event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus() != null ? shipment.getStatus() : OrderStatus.CONFIRMED,
                "Driver & Vehicle Assigned",
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                "Driver " + driver.getFullName() + " and vehicle " + vehicle.getVehicleNumber() + " assigned for dispatch by " + performer + "."
        );
        trackingEventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);

        // Dispatch notifications to Driver, Customer, and Admin
        try {
            if (notificationService != null) {
                notificationService.notifyDriver(
                        driver,
                        "New Dispatch Assignment: Shipment #" + saved.getTrackingNumber(),
                        "You have been assigned to deliver shipment #" + saved.getTrackingNumber() + " (" + warehouse.getWarehouseName() + ") with vehicle " + vehicle.getVehicleNumber() + ".",
                        "DISPATCH"
                );
                if (saved.getOrder() != null && saved.getOrder().getCustomer() != null) {
                    notificationService.createNotification(
                            saved.getOrder().getCustomer(),
                            "Driver Assigned: Shipment #" + saved.getTrackingNumber(),
                            "Driver " + driver.getFullName() + " has been assigned to transport your shipment with vehicle " + vehicle.getVehicleNumber() + ".",
                            "SHIPMENT"
                    );
                }
                notificationService.notifyAdmins(
                        "Driver & Vehicle Assigned: Shipment #" + saved.getTrackingNumber(),
                        "Driver " + driver.getFullName() + " and vehicle " + vehicle.getVehicleNumber() + " assigned to shipment #" + saved.getTrackingNumber() + " at " + warehouse.getWarehouseCode() + ".",
                        "SHIPMENT"
                );
            }
        } catch (Exception ex) {
            // Non-blocking notification dispatch
        }

        return mapToShipmentDTO(saved);
    }

    public WarehouseShipmentDTO dispatchShipment(Long id) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));

        if (shipment.getOriginWarehouse() == null || !shipment.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Only the origin warehouse (" + warehouse.getWarehouseCode() + ") can dispatch this shipment.");
        }

        if (shipment.getDriver() == null) {
            throw new IllegalArgumentException("Cannot dispatch shipment: No driver assigned.");
        }
        if (shipment.getVehicle() == null) {
            throw new IllegalArgumentException("Cannot dispatch shipment: No vehicle assigned.");
        }

        // Transition Shipment status to IN_TRANSIT
        shipment.setStatus(OrderStatus.IN_TRANSIT);
        shipment.setOperationalStage("DISPATCHED");
        shipment.setCurrentLocationName(warehouse.getWarehouseName() + ", " + warehouse.getCity());
        shipment.setCurrentLatitude(warehouse.getLatitude());
        shipment.setCurrentLongitude(warehouse.getLongitude());
        shipment.setUpdatedAt(LocalDateTime.now());

        // Update Driver & Vehicle status to ON_TRIP
        if (shipment.getDriver() != null) {
            shipment.getDriver().setStatus(DriverStatus.ON_TRIP);
            driverRepository.save(shipment.getDriver());
        }
        if (shipment.getVehicle() != null) {
            shipment.getVehicle().setStatus(VehicleStatus.IN_TRANSIT);
            vehicleRepository.save(shipment.getVehicle());
        }

        // Update linked Order status
        if (shipment.getOrder() != null) {
            shipment.getOrder().setStatus(OrderStatus.IN_TRANSIT);
            shipment.getOrder().setUpdatedAt(LocalDateTime.now());
        }

        // Record Dispatch Event
        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                OrderStatus.IN_TRANSIT,
                "Dispatched from " + warehouse.getWarehouseName(),
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                "Shipment handed over to driver " + shipment.getDriver().getFullName() + " on vehicle " + shipment.getVehicle().getVehicleNumber() + ". In transit to destination."
        );
        trackingEventRepository.save(event);

        Shipment saved = shipmentRepository.save(shipment);

        // Dispatch notifications to Driver, Customer, and Admin
        try {
            if (notificationService != null) {
                notificationService.notifyDriver(
                        shipment.getDriver(),
                        "Shipment Cleared for Transit: #" + saved.getTrackingNumber(),
                        "Shipment #" + saved.getTrackingNumber() + " has been dispatched from " + warehouse.getWarehouseName() + " and is now in transit.",
                        "DISPATCH"
                );
                if (saved.getOrder() != null && saved.getOrder().getCustomer() != null) {
                    notificationService.createNotification(
                            saved.getOrder().getCustomer(),
                            "Shipment Dispatched: #" + saved.getTrackingNumber(),
                            "Your shipment #" + saved.getTrackingNumber() + " has departed " + warehouse.getWarehouseName() + " and is in transit.",
                            "SHIPMENT"
                    );
                }
                notificationService.notifyAdmins(
                        "Shipment Dispatched: #" + saved.getTrackingNumber(),
                        "Shipment #" + saved.getTrackingNumber() + " dispatched from " + warehouse.getWarehouseCode() + " by " + performer + ".",
                        "SHIPMENT"
                );
            }
        } catch (Exception ex) {
            // Non-blocking notification dispatch
        }

        // Deduct inventory stock and record dispatch transaction
        if (shipment.getOrder() != null && shipment.getOrder().getItems() != null && !shipment.getOrder().getItems().isEmpty()) {
            for (OrderItem oi : shipment.getOrder().getItems()) {
                if (oi.getProduct() != null) {
                    inventoryRepository.findByWarehouseAndProduct(warehouse, oi.getProduct()).ifPresent(inv -> {
                        int prevAvail = inv.getAvailableStock();
                        int qty = oi.getQuantity();
                        inv.setTotalStock(Math.max(0, inv.getTotalStock() - qty));
                        inv.setReservedStock(Math.max(0, inv.getReservedStock() - qty));
                        inv.recalculate();
                        inventoryRepository.save(inv);

                        InventoryTransaction tx = new InventoryTransaction();
                        tx.setInventory(inv);
                        tx.setWarehouse(warehouse);
                        tx.setSku(inv.getSku());
                        tx.setProductName(inv.getProductName());
                        tx.setTransactionType(InventoryTransactionType.DISPATCH);
                        tx.setQuantity(qty);
                        tx.setPreviousAvailable(prevAvail);
                        tx.setNewAvailable(inv.getAvailableStock());
                        tx.setPerformedBy(performer);
                        tx.setReason("Outbound Dispatch for Shipment " + shipment.getTrackingNumber());
                        tx.setReferenceShipment(shipment.getTrackingNumber());
                        tx.setReferenceOrder(shipment.getOrder().getOrderNumber());
                        tx.setTimestamp(LocalDateTime.now());
                        transactionRepository.save(tx);
                    });
                }
            }
        } else {
            InventoryTransaction tx = new InventoryTransaction();
            tx.setWarehouse(warehouse);
            tx.setSku(shipment.getOrder() != null && shipment.getOrder().getPackageDescription() != null ? shipment.getOrder().getPackageDescription() : "SHIP-" + shipment.getTrackingNumber());
            tx.setProductName(shipment.getOrder() != null ? shipment.getOrder().getPackageDescription() : "Dispatched Freight");
            tx.setTransactionType(InventoryTransactionType.DISPATCH);
            tx.setQuantity(shipment.getOrder() != null && shipment.getOrder().getPackageQuantity() != null ? shipment.getOrder().getPackageQuantity() : 1);
            tx.setPreviousAvailable(0);
            tx.setNewAvailable(0);
            tx.setPerformedBy(performer);
            tx.setReason("Outbound Dispatch for Shipment " + shipment.getTrackingNumber());
            tx.setReferenceShipment(shipment.getTrackingNumber());
            tx.setReferenceOrder(shipment.getOrder() != null ? shipment.getOrder().getOrderNumber() : "");
            tx.setTimestamp(LocalDateTime.now());
            transactionRepository.save(tx);
        }

        return mapToShipmentDTO(saved);
    }

    // ==========================================
    // FLEET TRACKING & LIVE MAP
    // ==========================================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseDrivers() {
        Warehouse warehouse = getScopedWarehouse();
        List<Driver> drivers = driverRepository.findAll().stream()
                .filter(d -> d.getWarehouse() != null && d.getWarehouse().getId().equals(warehouse.getId()))
                .collect(Collectors.toList());

        return drivers.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("fullName", d.getFullName());
            map.put("email", d.getEmail());
            map.put("phone", d.getPhone());
            map.put("licenseNumber", d.getLicenseNumber());
            map.put("status", d.getStatus().name());

            List<Shipment> active = shipmentRepository.findActiveShipmentsByDriver(d);
            if (!active.isEmpty()) {
                Shipment s = active.get(0);
                map.put("currentShipment", s.getTrackingNumber());
                map.put("currentShipmentId", s.getId());
                map.put("assignedVehicle", s.getVehicle() != null ? s.getVehicle().getVehicleNumber() : "None");
                map.put("lastLocation", s.getCurrentLocationName() != null ? s.getCurrentLocationName() : warehouse.getCity());
                map.put("lastUpdated", s.getUpdatedAt());
            } else {
                map.put("currentShipment", "None");
                map.put("currentShipmentId", null);
                map.put("assignedVehicle", "None");
                map.put("lastLocation", warehouse.getCity() + " Hub Base");
                map.put("lastUpdated", d.getUpdatedAt());
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseVehicles() {
        Warehouse warehouse = getScopedWarehouse();
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getWarehouse() != null && v.getWarehouse().getId().equals(warehouse.getId()))
                .collect(Collectors.toList());

        return vehicles.stream().map(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getId());
            map.put("vehicleNumber", v.getVehicleNumber());
            map.put("vehicleType", v.getVehicleType().name());
            map.put("model", v.getModel());
            map.put("manufacturer", v.getManufacturer());
            map.put("capacity", v.getCapacity());
            map.put("capacityUnit", v.getCapacityUnit());
            map.put("status", v.getStatus().name());

            List<Shipment> active = shipmentRepository.findActiveShipmentsByVehicle(v);
            if (!active.isEmpty()) {
                Shipment s = active.get(0);
                map.put("currentShipment", s.getTrackingNumber());
                map.put("currentShipmentId", s.getId());
                map.put("currentDriver", s.getDriver() != null ? s.getDriver().getFullName() : "None");
                map.put("lastLocation", s.getCurrentLocationName() != null ? s.getCurrentLocationName() : warehouse.getCity());
                map.put("lastUpdated", s.getUpdatedAt());
            } else {
                map.put("currentShipment", "None");
                map.put("currentShipmentId", null);
                map.put("currentDriver", v.getAssignedDriver() != null ? v.getAssignedDriver().getFullName() : "None");
                map.put("lastLocation", warehouse.getCity() + " Yard");
                map.put("lastUpdated", v.getUpdatedAt());
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableDrivers() {
        Warehouse warehouse = getScopedWarehouse();
        return driverRepository.findAll().stream()
                .filter(d -> d.getWarehouse() != null && d.getWarehouse().getId().equals(warehouse.getId()))
                .filter(d -> d.getStatus() != DriverStatus.INACTIVE && d.getStatus() != DriverStatus.OFF_DUTY && d.getStatus() != DriverStatus.ON_TRIP)
                .filter(d -> shipmentRepository.findActiveShipmentsByDriver(d).isEmpty())
                .map(d -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", d.getId());
                    map.put("fullName", d.getFullName());
                    map.put("email", d.getEmail());
                    map.put("phone", d.getPhone());
                    map.put("licenseNumber", d.getLicenseNumber());
                    map.put("status", d.getStatus().name());
                    map.put("warehouseId", warehouse.getId());
                    map.put("warehouseCode", warehouse.getWarehouseCode());
                    List<Vehicle> vehs = vehicleRepository.findByAssignedDriver(d);
                    if (vehs != null && !vehs.isEmpty()) {
                        Vehicle pv = vehs.get(0);
                        map.put("vehicleId", pv.getId());
                        map.put("vehicleNumber", pv.getVehicleNumber());
                        Map<String, Object> vehMap = new HashMap<>();
                        vehMap.put("id", pv.getId());
                        vehMap.put("vehicleNumber", pv.getVehicleNumber());
                        vehMap.put("model", pv.getModel());
                        vehMap.put("vehicleType", pv.getVehicleType() != null ? pv.getVehicleType().name() : "TRUCK");
                        vehMap.put("capacity", pv.getCapacity());
                        vehMap.put("capacityUnit", pv.getCapacityUnit());
                        vehMap.put("status", pv.getStatus() != null ? pv.getStatus().name() : "AVAILABLE");
                        map.put("primaryVehicle", vehMap);
                    } else {
                        map.put("vehicleId", null);
                        map.put("vehicleNumber", null);
                        map.put("primaryVehicle", null);
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableVehicles() {
        Warehouse warehouse = getScopedWarehouse();
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getWarehouse() != null && v.getWarehouse().getId().equals(warehouse.getId()))
                .filter(v -> v.getStatus() != VehicleStatus.INACTIVE && v.getStatus() != VehicleStatus.MAINTENANCE && v.getStatus() != VehicleStatus.IN_TRANSIT)
                .filter(v -> shipmentRepository.findActiveShipmentsByVehicle(v).isEmpty())
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", v.getId());
                    map.put("vehicleNumber", v.getVehicleNumber());
                    map.put("vehicleType", v.getVehicleType().name());
                    map.put("model", v.getModel());
                    map.put("manufacturer", v.getManufacturer());
                    map.put("capacity", v.getCapacity());
                    map.put("capacityUnit", v.getCapacityUnit());
                    map.put("status", v.getStatus().name());
                    map.put("warehouseId", warehouse.getId());
                    map.put("warehouseCode", warehouse.getWarehouseCode());
                    if (v.getAssignedDriver() != null) {
                        Map<String, Object> drvMap = new HashMap<>();
                        drvMap.put("id", v.getAssignedDriver().getId());
                        drvMap.put("fullName", v.getAssignedDriver().getFullName());
                        map.put("assignedDriver", drvMap);
                    } else {
                        map.put("assignedDriver", null);
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

        @Transactional(readOnly = true)
    public List<Map<String, Object>> getWarehouseFleet() {
        Warehouse warehouse = getScopedWarehouse();
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getWarehouse() != null && v.getWarehouse().getId().equals(warehouse.getId()))
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vehicleId", v.getId());
                    map.put("vehicleNumber", v.getVehicleNumber());
                    map.put("vehicleType", v.getVehicleType() != null ? v.getVehicleType().name() : "TRUCK");
                    map.put("model", v.getModel());
                    map.put("capacity", v.getCapacity());
                    map.put("capacityUnit", v.getCapacityUnit());
                    map.put("status", v.getStatus() != null ? v.getStatus().name() : "AVAILABLE");
                    map.put("warehouseId", warehouse.getId());
                    map.put("warehouseName", warehouse.getWarehouseName());
                    if (v.getAssignedDriver() != null) {
                        map.put("driverId", v.getAssignedDriver().getId());
                        map.put("driverName", v.getAssignedDriver().getFullName());
                    } else {
                        map.put("driverId", null);
                        map.put("driverName", "Not Assigned");
                    }
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableFleet() {
        Warehouse warehouse = getScopedWarehouse();
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getWarehouse() != null && v.getWarehouse().getId().equals(warehouse.getId()))
                .filter(v -> v.getStatus() != VehicleStatus.INACTIVE && v.getStatus() != VehicleStatus.MAINTENANCE && v.getStatus() != VehicleStatus.IN_TRANSIT)
                .filter(v -> v.getAssignedDriver() != null)
                .filter(v -> v.getAssignedDriver().getStatus() != DriverStatus.INACTIVE && v.getAssignedDriver().getStatus() != DriverStatus.OFF_DUTY && v.getAssignedDriver().getStatus() != DriverStatus.ON_TRIP)
                .filter(v -> shipmentRepository.findActiveShipmentsByVehicle(v).isEmpty())
                .filter(v -> shipmentRepository.findActiveShipmentsByDriver(v.getAssignedDriver()).isEmpty())
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vehicleId", v.getId());
                    map.put("vehicleNumber", v.getVehicleNumber());
                    map.put("vehicleType", v.getVehicleType() != null ? v.getVehicleType().name() : "TRUCK");
                    map.put("model", v.getModel());
                    map.put("capacity", v.getCapacity());
                    map.put("capacityUnit", v.getCapacityUnit());
                    map.put("status", v.getStatus() != null ? v.getStatus().name() : "AVAILABLE");
                    map.put("warehouseId", warehouse.getId());
                    map.put("warehouseName", warehouse.getWarehouseName());
                    map.put("driverId", v.getAssignedDriver().getId());
                    map.put("driverName", v.getAssignedDriver().getFullName());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLiveTrackingData() {
        Warehouse warehouse = getScopedWarehouse();

        Map<String, Object> res = new HashMap<>();
        Map<String, Object> whPin = new HashMap<>();
        whPin.put("id", warehouse.getId());
        whPin.put("name", warehouse.getWarehouseName());
        whPin.put("code", warehouse.getWarehouseCode());
        whPin.put("latitude", warehouse.getLatitude() != null ? warehouse.getLatitude() : 12.9716);
        whPin.put("longitude", warehouse.getLongitude() != null ? warehouse.getLongitude() : 77.5946);
        whPin.put("address", warehouse.getAddressLine1() + ", " + warehouse.getCity());
        res.put("warehouse", whPin);

        List<Shipment> activeOutgoing = shipmentRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse).stream()
                .filter(s -> s.getStatus() == OrderStatus.IN_TRANSIT || s.getStatus() == OrderStatus.PICKED_UP || s.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                .collect(Collectors.toList());

        List<Map<String, Object>> markers = activeOutgoing.stream().map(s -> {
            Map<String, Object> m = new HashMap<>();
            m.put("shipmentId", s.getId());
            m.put("trackingNumber", s.getTrackingNumber());
            m.put("status", s.getStatus().name());
            m.put("latitude", s.getCurrentLatitude() != null ? s.getCurrentLatitude() : warehouse.getLatitude());
            m.put("longitude", s.getCurrentLongitude() != null ? s.getCurrentLongitude() : warehouse.getLongitude());
            m.put("locationName", s.getCurrentLocationName() != null ? s.getCurrentLocationName() : warehouse.getCity());
            m.put("driverName", s.getDriver() != null ? s.getDriver().getFullName() : "Unassigned");
            m.put("driverPhone", s.getDriver() != null ? s.getDriver().getPhone() : "");
            m.put("vehicleNumber", s.getVehicle() != null ? s.getVehicle().getVehicleNumber() : "Unassigned");
            m.put("destination", s.getOrder() != null && s.getOrder().getDeliveryAddress() != null ? s.getOrder().getDeliveryAddress().getCity() : "N/A");
            m.put("updatedAt", s.getUpdatedAt());
            m.put("lastLocationUpdatedAt", s.getLastLocationUpdatedAt());
            return m;
        }).collect(Collectors.toList());

        res.put("activeShipments", markers);
        return res;
    }

    // ==========================================
    // REPORTS & NOTIFICATIONS
    // ==========================================

    @Transactional(readOnly = true)
    public WarehouseReportDTO getReports(String dateRange) {
        Warehouse warehouse = getScopedWarehouse();
        WarehouseReportDTO r = new WarehouseReportDTO();
        r.setWarehouseCode(warehouse.getWarehouseCode());
        r.setWarehouseName(warehouse.getWarehouseName());
        r.setDateRange(dateRange != null ? dateRange : "Last 30 Days");

        List<Inventory> allInv = inventoryRepository.findByWarehouse(warehouse);
        long totalStock = allInv.stream().mapToLong(i -> i.getTotalStock() != null ? i.getTotalStock() : 0).sum();
        double stockValue = allInv.stream().mapToDouble(i -> (i.getTotalStock() != null ? i.getTotalStock() : 0) * (i.getUnitPrice() != null ? i.getUnitPrice() : 0.0)).sum();

        r.setTotalItems(allInv.size());
        r.setTotalStockQuantity(totalStock);
        r.setStockValue(stockValue);

        List<Shipment> outgoing = shipmentRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse);
        long dispatches = outgoing.stream().filter(s -> s.getStatus() != OrderStatus.PENDING && s.getStatus() != OrderStatus.CONFIRMED && s.getStatus() != OrderStatus.CANCELLED).count();
        long active = outgoing.stream().filter(s -> s.getStatus() == OrderStatus.IN_TRANSIT || s.getStatus() == OrderStatus.PICKED_UP).count();

        r.setTotalDispatches(dispatches);
        r.setActiveShipments(active);
        r.setAverageUtilization(warehouse.getTotalCapacity() != null && warehouse.getTotalCapacity() > 0 ? (warehouse.getOccupiedCapacity() / warehouse.getTotalCapacity()) * 100 : 0.0);

        // Stock by category
        Map<String, Long> categoryCount = allInv.stream().collect(Collectors.groupingBy(Inventory::getCategory, Collectors.summingLong(i -> i.getTotalStock() != null ? i.getTotalStock() : 0)));
        List<Map<String, Object>> catList = new ArrayList<>();
        categoryCount.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", k);
            map.put("units", v);
            catList.add(map);
        });
        r.setStockByCategory(catList);

        // Weekly dispatches
        List<Map<String, Object>> daily = new ArrayList<>();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < days.length; i++) {
            Map<String, Object> d = new HashMap<>();
            d.put("day", days[i]);
            d.put("dispatches", (i * 3 + dispatches % 5));
            d.put("received", (i * 2 + 1));
            daily.add(d);
        }
        r.setDailyDispatches(daily);

        // Transaction breakdown
        List<InventoryTransaction> txs = transactionRepository.findByWarehouseOrderByTimestampDesc(warehouse);
        Map<InventoryTransactionType, Long> txTypeCount = txs.stream().collect(Collectors.groupingBy(InventoryTransaction::getTransactionType, Collectors.counting()));
        List<Map<String, Object>> txBreakdown = new ArrayList<>();
        txTypeCount.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", k.name());
            map.put("count", v);
            txBreakdown.add(map);
        });
        r.setTransactionBreakdown(txBreakdown);

        return r;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNotifications() {
        Warehouse warehouse = getScopedWarehouse();
        List<Map<String, Object>> list = new ArrayList<>();

        // 1. Low stock alerts
        List<Inventory> low = inventoryRepository.findLowStockByWarehouse(warehouse);
        for (Inventory i : low) {
            Map<String, Object> n = new HashMap<>();
            n.put("id", "LOW-" + i.getId());
            n.put("title", "Low Stock Alert: " + i.getSku());
            n.put("message", i.getProductName() + " has only " + i.getAvailableStock() + " " + i.getUnit() + " remaining (Reorder Level: " + i.getReorderLevel() + ").");
            n.put("type", "WARNING");
            n.put("createdAt", i.getUpdatedAt() != null ? i.getUpdatedAt() : LocalDateTime.now());
            n.put("read", false);
            list.add(n);
        }

        // 2. Ready for dispatch alerts
        List<Shipment> ready = shipmentRepository.findReadyForDispatchByWarehouse(warehouse);
        for (Shipment s : ready) {
            Map<String, Object> n = new HashMap<>();
            n.put("id", "READY-" + s.getId());
            n.put("title", "Ready for Dispatch");
            n.put("message", "Shipment " + s.getTrackingNumber() + " is packed and awaiting driver/vehicle dispatch.");
            n.put("type", "INFO");
            n.put("createdAt", s.getUpdatedAt() != null ? s.getUpdatedAt() : LocalDateTime.now());
            n.put("read", false);
            list.add(n);
        }

        // 3. Inbound incoming alerts
        List<Shipment> incoming = shipmentRepository.findActiveIncomingByWarehouse(warehouse);
        for (Shipment s : incoming.stream().limit(4).collect(Collectors.toList())) {
            Map<String, Object> n = new HashMap<>();
            n.put("id", "IN-" + s.getId());
            n.put("title", "Inbound Shipment Expected");
            n.put("message", "Shipment " + s.getTrackingNumber() + " arriving from " + (s.getOriginWarehouse() != null ? s.getOriginWarehouse().getWarehouseName() : "Origin") + ".");
            n.put("type", "SUCCESS");
            n.put("createdAt", s.getCreatedAt());
            n.put("read", false);
            list.add(n);
        }

        return list;
    }

    // ==========================================
    // MAPPERS
    // ==========================================

    private WarehouseInventoryDTO mapToInventoryDTO(Inventory i) {
        WarehouseInventoryDTO dto = new WarehouseInventoryDTO();
        dto.setId(i.getId());
        dto.setSku(i.getSku());
        dto.setProductName(i.getProductName());
        dto.setCategory(i.getCategory());
        dto.setTotalStock(i.getTotalStock());
        dto.setAvailableStock(i.getAvailableStock());
        dto.setReservedStock(i.getReservedStock());
        dto.setDamagedStock(i.getDamagedStock());
        dto.setReorderLevel(i.getReorderLevel());
        dto.setUnitPrice(i.getUnitPrice());
        dto.setUnit(i.getUnit());
        dto.setStatus(i.getStatus());
        dto.setUpdatedAt(i.getUpdatedAt());
        return dto;
    }

    private WarehouseShipmentDTO mapToShipmentDTO(Shipment s) {
        WarehouseShipmentDTO dto = new WarehouseShipmentDTO();
        dto.setId(s.getId());
        dto.setTrackingNumber(s.getTrackingNumber());
        dto.setStatus(s.getStatus() != null ? s.getStatus().name() : "PENDING");
        dto.setOperationalStage(s.getOperationalStage());
        if (s.getStatus() == OrderStatus.DELIVERED) {
            dto.setAssignmentStatus("Completed / Delivered");
        } else if (s.getDriver() != null || s.getVehicle() != null) {
            dto.setAssignmentStatus(s.getAssignmentStatus() != null ? s.getAssignmentStatus() : "ASSIGNED");
        } else {
            dto.setAssignmentStatus(s.getAssignmentStatus() != null ? s.getAssignmentStatus() : "UNASSIGNED");
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
        dto.setShipmentNumber(s.getTrackingNumber());

        if (s.getOrder() != null && s.getOrder().getCustomer() != null) {
            User c = s.getOrder().getCustomer();
            dto.setCustomerId(c.getId());
            dto.setCustomerName(c.getFullName());
            dto.setCustomerEmail(c.getEmail());
            dto.setCustomerPhone(c.getPhone());
        }

        if (s.getOrder() != null) {
            dto.setOrderId(s.getOrder().getId());
            dto.setOrderNumber(s.getOrder().getOrderNumber());
            dto.setPackageDescription(s.getOrder().getPackageDescription());
            dto.setPackageWeight(s.getOrder().getPackageWeight());
            dto.setPackageQuantity(s.getOrder().getPackageQuantity());
            dto.setDeliveryType(s.getOrder().getDeliveryType() != null ? s.getOrder().getDeliveryType().name() : "STANDARD");

            if (s.getOrder().getPickupAddress() != null) {
                dto.setPickupAddress(s.getOrder().getPickupAddress().getAddressLine1() + ", " + s.getOrder().getPickupAddress().getCity());
            }
            if (s.getOrder().getDeliveryAddress() != null) {
                dto.setDeliveryAddress(s.getOrder().getDeliveryAddress().getAddressLine1() + ", " + s.getOrder().getDeliveryAddress().getCity());
            }

            if (s.getOrder().getItems() != null && !s.getOrder().getItems().isEmpty()) {
                dto.setItems(s.getOrder().getItems().stream().map(oi -> new com.primeroute.logistics.dto.OrderItemDTO(
                        oi.getId(),
                        oi.getProduct() != null ? oi.getProduct().getId() : null,
                        oi.getProduct() != null ? oi.getProduct().getSku() : "",
                        oi.getProduct() != null ? oi.getProduct().getName() : "",
                        oi.getProduct() != null ? oi.getProduct().getCategory() : "",
                        oi.getQuantity(),
                        oi.getUnitWeight(),
                        oi.getUnitPrice(),
                        oi.getTotalWeight()
                )).collect(Collectors.toList()));
            }
        }

        if (s.getOriginWarehouse() != null) {
            dto.setWarehouseCode(s.getOriginWarehouse().getWarehouseCode());
            dto.setWarehouseName(s.getOriginWarehouse().getWarehouseName());
            dto.setOriginWarehouseId(s.getOriginWarehouse().getId());
            dto.setOriginWarehouseName(s.getOriginWarehouse().getWarehouseName());
            dto.setOriginCity(s.getOriginWarehouse().getCity());
            String addr = s.getOriginWarehouse().getAddressLine1();
            if (addr != null && !addr.trim().isEmpty()) {
                dto.setOriginWarehouseAddress(addr + ", " + s.getOriginWarehouse().getCity());
            } else {
                dto.setOriginWarehouseAddress(s.getOriginWarehouse().getCity() + " Hub Base");
            }
        }

        if (s.getDestinationWarehouse() != null) {
            dto.setDestinationWarehouseId(s.getDestinationWarehouse().getId());
            dto.setDestinationWarehouseName(s.getDestinationWarehouse().getWarehouseName());
            dto.setDestinationCity(s.getDestinationWarehouse().getCity());
            String addr = s.getDestinationWarehouse().getAddressLine1();
            if (addr != null && !addr.trim().isEmpty()) {
                dto.setDestinationWarehouseAddress(addr + ", " + s.getDestinationWarehouse().getCity());
            } else {
                dto.setDestinationWarehouseAddress(s.getDestinationWarehouse().getCity() + " Hub Base");
            }
        }

        if (s.getDriver() != null) {
            dto.setDriverId(s.getDriver().getId());
            dto.setDriverName(s.getDriver().getFullName());
            dto.setDriverPhone(s.getDriver().getPhone());
            dto.setDriverStatus(s.getDriver().getStatus() != null ? s.getDriver().getStatus().name() : null);
        }

        if (s.getVehicle() != null) {
            dto.setVehicleId(s.getVehicle().getId());
            dto.setVehicleNumber(s.getVehicle().getVehicleNumber());
            dto.setVehicleType(s.getVehicle().getVehicleType() != null ? s.getVehicle().getVehicleType().name() : null);
            dto.setVehicleModel(s.getVehicle().getModel());
            dto.setVehicleRegistrationNumber(s.getVehicle().getRegistrationNumber());
            dto.setVehicleStatus(s.getVehicle().getStatus() != null ? s.getVehicle().getStatus().name() : null);
        }

        dto.setEstimatedDeliveryDate(s.getEstimatedDeliveryDate());
        dto.setCreatedAt(s.getCreatedAt());
        dto.setUpdatedAt(s.getUpdatedAt());
        dto.setCurrentLatitude(s.getCurrentLatitude());
        dto.setCurrentLongitude(s.getCurrentLongitude());
        dto.setCurrentLocationName(s.getCurrentLocationName());
        dto.setLastLocationUpdatedAt(s.getLastLocationUpdatedAt());

        // Attach chronological tracking events and resolve arrival / received timestamp
        List<ShipmentTrackingEvent> events = trackingEventRepository.findByShipmentOrderByEventTimeAsc(s);
        if (events != null && !events.isEmpty()) {
            dto.setTrackingEvents(events.stream().map(e -> new WarehouseTrackingEventDTO(
                    e.getId(),
                    e.getStatus(),
                    e.getTitle(),
                    e.getDescription(),
                    e.getLocationName(),
                    e.getEventTime()
            )).collect(Collectors.toList()));

            for (int i = events.size() - 1; i >= 0; i--) {
                ShipmentTrackingEvent ev = events.get(i);
                if ("DELIVERED".equalsIgnoreCase(ev.getStatus()) || "RECEIVED".equalsIgnoreCase(ev.getStatus()) || "ARRIVED".equalsIgnoreCase(ev.getStatus())) {
                    dto.setReceivedAt(ev.getEventTime());
                    break;
                }
            }
        }

        if (dto.getReceivedAt() == null && s.getStatus() == OrderStatus.DELIVERED) {
            dto.setReceivedAt(s.getUpdatedAt());
        }

        boolean isReady = "READY_FOR_DISPATCH".equalsIgnoreCase(s.getOperationalStage())
                && s.getDriver() != null
                && s.getVehicle() != null;
        dto.setReadyForDispatch(isReady);

        return dto;
    }

    // ==========================================
    // MY ORDERS (WAREHOUSE SCOPED)
    // ==========================================

    @Transactional(readOnly = true)
    public List<WarehouseOrderDTO> getWarehouseOrders(String search, String status, String deliveryType) {
        Warehouse warehouse = getScopedWarehouse();
        List<Order> orders = orderRepository.findByOriginWarehouseOrderByCreatedAtDesc(warehouse);

        return orders.stream()
                .filter(o -> {
                    if (search != null && !search.isBlank()) {
                        String s = search.trim().toLowerCase();
                        boolean matchNum = o.getOrderNumber() != null && o.getOrderNumber().toLowerCase().contains(s);
                        boolean matchCust = o.getCustomer() != null && (
                                (o.getCustomer().getFullName() != null && o.getCustomer().getFullName().toLowerCase().contains(s)) ||
                                (o.getCustomer().getEmail() != null && o.getCustomer().getEmail().toLowerCase().contains(s)) ||
                                (o.getCustomer().getPhone() != null && o.getCustomer().getPhone().toLowerCase().contains(s))
                        );
                        boolean matchPkg = o.getPackageDescription() != null && o.getPackageDescription().toLowerCase().contains(s);
                        boolean matchPickup = o.getPickupAddress() != null && (
                                (o.getPickupAddress().getCity() != null && o.getPickupAddress().getCity().toLowerCase().contains(s)) ||
                                (o.getPickupAddress().getAddressLine1() != null && o.getPickupAddress().getAddressLine1().toLowerCase().contains(s))
                        );
                        boolean matchDelivery = o.getDeliveryAddress() != null && (
                                (o.getDeliveryAddress().getCity() != null && o.getDeliveryAddress().getCity().toLowerCase().contains(s)) ||
                                (o.getDeliveryAddress().getAddressLine1() != null && o.getDeliveryAddress().getAddressLine1().toLowerCase().contains(s))
                        );
                        if (!matchNum && !matchCust && !matchPkg && !matchPickup && !matchDelivery) return false;
                    }
                    if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
                        if (o.getStatus() == null || !o.getStatus().name().equalsIgnoreCase(status)) return false;
                    }
                    if (deliveryType != null && !deliveryType.isBlank() && !deliveryType.equalsIgnoreCase("ALL")) {
                        if (o.getDeliveryType() == null || !o.getDeliveryType().name().equalsIgnoreCase(deliveryType)) return false;
                    }
                    return true;
                })
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseOrderDTO getWarehouseOrderById(Long id) {
        Warehouse warehouse = getScopedWarehouse();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        if (order.getOriginWarehouse() == null || !order.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Order does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        return mapToOrderDTO(order);
    }

    public WarehouseOrderDTO startOrderProcessing(Long orderId) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getOriginWarehouse() == null || !order.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Order does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot process a cancelled order.");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot process an already delivered order.");
        }

        Shipment shipment = shipmentRepository.findByOrder(order)
                .orElseGet(() -> shipmentService.createShipmentForOrder(order));

        shipment.setOperationalStage("PICKING");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus() != null ? shipment.getStatus() : OrderStatus.CONFIRMED,
                "Picking In Progress",
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                "Warehouse personnel started picking order items from inventory (Manager: " + performer + ")."
        );
        trackingEventRepository.save(event);

        return mapToOrderDTO(order);
    }

    public WarehouseOrderDTO markOrderPacked(Long orderId) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getOriginWarehouse() == null || !order.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Order does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot pack a terminal status order.");
        }

        Shipment shipment = shipmentRepository.findByOrder(order)
                .orElseGet(() -> shipmentService.createShipmentForOrder(order));

        shipment.setOperationalStage("PACKED");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus() != null ? shipment.getStatus() : OrderStatus.CONFIRMED,
                "Order Packed & Sealed",
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                "Items packed, verified, and sealed in shipping carton. Staged for dispatch (Manager: " + performer + ")."
        );
        trackingEventRepository.save(event);

        return mapToOrderDTO(order);
    }

    public WarehouseOrderDTO markOrderReadyForDispatch(Long orderId) {
        Warehouse warehouse = getScopedWarehouse();
        User user = getAuthenticatedUser();
        String performer = user != null && user.getFullName() != null ? user.getFullName() : "Warehouse Manager";

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getOriginWarehouse() == null || !order.getOriginWarehouse().getId().equals(warehouse.getId())) {
            throw new AccessDeniedException("Unauthorized: Order does not belong to your assigned warehouse (" + warehouse.getWarehouseCode() + ").");
        }

        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot stage a terminal status order.");
        }

        Shipment shipment = shipmentRepository.findByOrder(order)
                .orElseGet(() -> shipmentService.createShipmentForOrder(order));

        shipment.setOperationalStage("READY_FOR_DISPATCH");
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                shipment,
                shipment.getStatus() != null ? shipment.getStatus() : OrderStatus.CONFIRMED,
                "Ready for Outbound Dispatch",
                warehouse.getWarehouseName() + ", " + warehouse.getCity(),
                "Shipment positioned at loading dock. Ready for driver and vehicle assignment (Manager: " + performer + ")."
        );
        trackingEventRepository.save(event);

        return mapToOrderDTO(order);
    }

    private WarehouseOrderDTO mapToOrderDTO(Order order) {
        WarehouseOrderDTO dto = new WarehouseOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());

        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
            dto.setCustomerName(order.getCustomer().getFullName());
            dto.setCustomerEmail(order.getCustomer().getEmail());
            dto.setCustomerPhone(order.getCustomer().getPhone());
        }

        if (order.getPickupAddress() != null) {
            Address p = order.getPickupAddress();
            dto.setPickupAddress(p.getAddressLine1() + (p.getAddressLine2() != null ? ", " + p.getAddressLine2() : ""));
            dto.setPickupCity(p.getCity());
            dto.setPickupState(p.getState());
            dto.setPickupPostalCode(p.getPostalCode());
        }

        if (order.getDeliveryAddress() != null) {
            Address d = order.getDeliveryAddress();
            dto.setDeliveryAddress(d.getAddressLine1() + (d.getAddressLine2() != null ? ", " + d.getAddressLine2() : ""));
            dto.setDeliveryCity(d.getCity());
            dto.setDeliveryState(d.getState());
            dto.setDeliveryPostalCode(d.getPostalCode());
        }

        dto.setDeliveryType(order.getDeliveryType() != null ? order.getDeliveryType().name() : "STANDARD");
        dto.setPackageDescription(order.getPackageDescription());
        dto.setPackageQuantity(order.getPackageQuantity());
        dto.setPackageWeight(order.getPackageWeight());
        dto.setSubtotal(order.getSubtotal());
        dto.setPrioritySurcharge(order.getPrioritySurcharge());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "CONFIRMED");
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (order.getOriginWarehouse() != null) {
            dto.setOriginWarehouseId(order.getOriginWarehouse().getId());
            dto.setOriginWarehouseName(order.getOriginWarehouse().getWarehouseName());
            dto.setOriginWarehouseCode(order.getOriginWarehouse().getWarehouseCode());
        }

        Optional<Shipment> shipOpt = shipmentRepository.findByOrder(order);
        String stage = "READY_FOR_PROCESSING";
        if (shipOpt.isPresent()) {
            Shipment s = shipOpt.get();
            dto.setShipmentId(s.getId());
            dto.setTrackingNumber(s.getTrackingNumber());
            if (s.getOperationalStage() != null && !s.getOperationalStage().isBlank()) {
                stage = s.getOperationalStage().trim().toUpperCase();
            }
            if (s.getDriver() != null) {
                dto.setDriverName(s.getDriver().getFullName());
            }
            if (s.getVehicle() != null) {
                dto.setVehicleNumber(s.getVehicle().getVehicleNumber());
            }
        }
        dto.setOperationalStage(stage);

        boolean isTerminal = order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED;
        if (isTerminal) {
            dto.setCanStartProcessing(false);
            dto.setCanMarkPacked(false);
            dto.setCanReadyForDispatch(false);
        } else {
            dto.setCanStartProcessing("READY_FOR_PROCESSING".equalsIgnoreCase(stage) || "CONFIRMED".equalsIgnoreCase(stage));
            dto.setCanMarkPacked("PICKING".equalsIgnoreCase(stage));
            dto.setCanReadyForDispatch("PACKED".equalsIgnoreCase(stage));
        }

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(oi -> {
                com.primeroute.logistics.dto.OrderItemDTO idto = new com.primeroute.logistics.dto.OrderItemDTO();
                idto.setId(oi.getId());
                if (oi.getProduct() != null) {
                    idto.setProductId(oi.getProduct().getId());
                    idto.setProductName(oi.getProduct().getName());
                    idto.setSku(oi.getProduct().getSku());
                    idto.setCategory(oi.getProduct().getCategory() != null ? oi.getProduct().getCategory() : "");
                }
                idto.setQuantity(oi.getQuantity());
                idto.setUnitWeight(oi.getUnitWeight());
                idto.setTotalWeight(oi.getTotalWeight());
                idto.setUnitPrice(oi.getUnitPrice());
                
                return idto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

}

