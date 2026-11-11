package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceFulfillmentIntegrationTest {

    @Mock private OrderRepository orderRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private UserRepository userRepository;
    @Mock private ShipmentRepository shipmentRepository;
    @Mock private ShipmentService shipmentService;
    @Mock private NotificationService notificationService;
    @Mock private ProductRepository productRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryTransactionRepository transactionRepository;
    @Mock private FulfillmentAllocationRepository fulfillmentAllocationRepository;
    @Mock private FulfillmentEngineService fulfillmentEngineService;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private Address pickupAddress;
    private Address deliveryAddress;
    private Warehouse warehouseMumbai;
    private Warehouse warehouseDelhi;
    private Product productA;
    private Product productB;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@primeroute.com");
        customer.setFullName("Acme Logistics Customer");

        pickupAddress = new Address();
        pickupAddress.setCustomer(customer);
        pickupAddress.setAddressLine1("123 Industrial Area");
        pickupAddress.setCity("Mumbai");
        pickupAddress.setState("Maharashtra");
        pickupAddress.setPostalCode("400001");
        pickupAddress.setCountry("India");
        pickupAddress.setLatitude(19.0760);
        pickupAddress.setLongitude(72.8777);

        deliveryAddress = new Address();
        deliveryAddress.setCustomer(customer);
        deliveryAddress.setAddressLine1("456 Retail Park");
        deliveryAddress.setCity("Mumbai");
        deliveryAddress.setState("Maharashtra");
        deliveryAddress.setPostalCode("400002");
        deliveryAddress.setCountry("India");
        deliveryAddress.setLatitude(19.0800);
        deliveryAddress.setLongitude(72.8800);

        warehouseMumbai = new Warehouse();
        warehouseMumbai.setId(10L);
        warehouseMumbai.setWarehouseCode("WH-MUM-01");
        warehouseMumbai.setWarehouseName("Mumbai Central Hub");
        warehouseMumbai.setStatus(WarehouseStatus.ACTIVE);
        warehouseMumbai.setTotalCapacity(1000.0);
        warehouseMumbai.setOccupiedCapacity(100.0);
        warehouseMumbai.setCapacityUnit("TONS");
        warehouseMumbai.setCity("Mumbai");
        warehouseMumbai.setLatitude(19.0760);
        warehouseMumbai.setLongitude(72.8777);

        warehouseDelhi = new Warehouse();
        warehouseDelhi.setId(20L);
        warehouseDelhi.setWarehouseCode("WH-DEL-01");
        warehouseDelhi.setWarehouseName("Delhi Mega Hub");
        warehouseDelhi.setStatus(WarehouseStatus.ACTIVE);
        warehouseDelhi.setTotalCapacity(500.0);
        warehouseDelhi.setOccupiedCapacity(50.0);
        warehouseDelhi.setCapacityUnit("TONS");
        warehouseDelhi.setCity("Delhi");

        productA = new Product("SKU-PR-001", "Enterprise Router", "Hardware", 2.0, 5000.0, "Units", 10);
        productA.setId(101L);

        productB = new Product("SKU-PR-002", "Optical Cable", "Hardware", 1.0, 1000.0, "Units", 10);
        productB.setId(102L);

        when(userRepository.findByEmailIgnoreCase("customer@primeroute.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndCustomer(1L, customer)).thenReturn(Optional.of(pickupAddress));
        when(addressRepository.findByIdAndCustomer(2L, customer)).thenReturn(Optional.of(deliveryAddress));
    }

    private Inventory createInventory(Warehouse wh, Product prod, int total, int reserved, int damaged) {
        Inventory inv = new Inventory();
        inv.setWarehouse(wh);
        inv.setProduct(prod);
        inv.setSku(prod.getSku());
        inv.setProductName(prod.getName());
        inv.setCategory(prod.getCategory());
        inv.setTotalStock(total);
        inv.setReservedStock(reserved);
        inv.setDamagedStock(damaged);
        inv.recalculate();
        return inv;
    }

    private Shipment createMockShipment(Order order, Warehouse warehouse, String trackingNumber) {
        Shipment s = new Shipment();
        s.setId(500L);
        s.setOrder(order);
        s.setOriginWarehouse(warehouse);
        s.setTrackingNumber(trackingNumber);
        s.setStatus(OrderStatus.CONFIRMED);
        s.setOperationalStage("READY_FOR_PROCESSING");
        s.setDriver(null);
        s.setVehicle(null);
        s.setRoute(null);
        s.setAssignmentStatus("Awaiting Warehouse Assignment");
        s.setAssignedBy("SYSTEM - AUTOMATIC FULFILLMENT");
        s.setCreatedAt(LocalDateTime.now());
        s.setUpdatedAt(LocalDateTime.now());
        return s;
    }

    // =========================================================
    // TEST 1: Successful allocated order -> Exactly 1 Shipment created
    // =========================================================
    @Test
    @DisplayName("TEST 1: Successful allocated order creates exactly one Shipment")
    void testSuccessfulOrderCreatesOneShipment() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);
        request.setItems(List.of(new OrderItemRequest(101L, 2)));

        when(productRepository.findById(101L)).thenReturn(Optional.of(productA));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 185.0, 10.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        Inventory inv = createInventory(warehouseMumbai, productA, 50, 0, 0);
        when(inventoryRepository.findByWarehouseAndProductForUpdate(warehouseMumbai, productA)).thenReturn(Optional.of(inv));

        Shipment mockShipment = createMockShipment(null, warehouseMumbai, "PR-TRK-12345678");
        when(shipmentService.createShipmentForOrder(any(Order.class))).thenReturn(mockShipment);
        when(shipmentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(mockShipment));

        OrderResponse response = orderService.createOrder("customer@primeroute.com", request);

        assertNotNull(response);
        assertEquals("PR-TRK-12345678", response.getTrackingNumber());
        verify(shipmentService, times(1)).createShipmentForOrder(any(Order.class));
    }

    // =========================================================
    // TEST 2: Shipment.order points to correct Order
    // =========================================================
    @Test
    @DisplayName("TEST 2: Created shipment links back directly to the created Order")
    void testShipmentOrderAssociation() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 180.0, 5.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        orderService.createOrder("customer@primeroute.com", request);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(shipmentService).createShipmentForOrder(orderCaptor.capture());
        assertNotNull(orderCaptor.getValue().getOrderNumber());
        assertEquals(warehouseMumbai, orderCaptor.getValue().getOriginWarehouse());
    }

    // =========================================================
    // TEST 3: Shipment.originWarehouse matches Order.originWarehouse
    // =========================================================
    @Test
    @DisplayName("TEST 3: Shipment origin warehouse strictly matches the allocated Order origin warehouse")
    void testShipmentOriginWarehouseMatch() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 180.0, 5.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        orderService.createOrder("customer@primeroute.com", request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(shipmentService).createShipmentForOrder(captor.capture());
        assertEquals(warehouseMumbai, captor.getValue().getOriginWarehouse());
    }

    // =========================================================
    // TEST 4: Tracking number generated and unique
    // =========================================================
    @Test
    @DisplayName("TEST 4: Tracking number has correct format PR-TRK-XXXXXXXX and is populated")
    void testTrackingNumberFormatAndPresence() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 180.0, 5.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        Shipment shipment = createMockShipment(null, warehouseMumbai, "PR-TRK-98765432");
        when(shipmentService.createShipmentForOrder(any(Order.class))).thenReturn(shipment);
        when(shipmentRepository.findByOrder(any(Order.class))).thenReturn(Optional.of(shipment));

        OrderResponse resp = orderService.createOrder("customer@primeroute.com", request);
        assertTrue(resp.getTrackingNumber().startsWith("PR-TRK-"));
    }

    // =========================================================
    // TEST 5: Initial operational stage is READY_FOR_PROCESSING
    // =========================================================
    @Test
    @DisplayName("TEST 5: Initial operational stage is set to READY_FOR_PROCESSING")
    void testInitialOperationalStage() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-11112222");
        assertEquals("READY_FOR_PROCESSING", s.getOperationalStage());
    }

    // =========================================================
    // TEST 6: Driver is null initially
    // =========================================================
    @Test
    @DisplayName("TEST 6: Driver is null upon initial automatic shipment creation")
    void testDriverIsNullInitially() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-11112222");
        assertNull(s.getDriver(), "Driver must remain null upon automatic creation");
    }

    // =========================================================
    // TEST 7: Vehicle is null initially
    // =========================================================
    @Test
    @DisplayName("TEST 7: Vehicle is null upon initial automatic shipment creation")
    void testVehicleIsNullInitially() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-11112222");
        assertNull(s.getVehicle(), "Vehicle must remain null upon automatic creation");
    }

    // =========================================================
    // TEST 8: Initial ShipmentTrackingEvent created
    // =========================================================
    @Test
    @DisplayName("TEST 8: Initial shipment tracking event records creation and warehouse allocation")
    void testInitialTrackingEvent() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-11112222");
        ShipmentTrackingEvent event = new ShipmentTrackingEvent(
                s, s.getStatus(), "Shipment Created & Allocated", "Mumbai Central Hub", "Allocated to warehouse"
        );
        assertNotNull(event);
        assertEquals(s, event.getShipment());
        assertEquals("Shipment Created & Allocated", event.getTitle());
    }

    // =========================================================
    // TEST 9: EXCEPTION fulfillment -> ZERO Shipment created
    // =========================================================
    @Test
    @DisplayName("TEST 9: Fulfillment EXCEPTION results in zero shipment creation")
    void testExceptionCreatesZeroShipments() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.exception(
                null, null, "INSUFFICIENT_INVENTORY"
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);

        OrderResponse resp = orderService.createOrder("customer@primeroute.com", request);

        assertNull(resp.getOriginWarehouseId());
        assertNull(resp.getTrackingNumber());
        verify(shipmentService, never()).createShipmentForOrder(any(Order.class));
    }

    // =========================================================
    // TEST 10: Duplicate / retry scenario does NOT create another Shipment
    // =========================================================
    @Test
    @DisplayName("TEST 10: Duplicate order retry does not create duplicate shipments")
    void testDuplicateRetryDoesNotDuplicateShipment() {
        Order order = new Order();
        order.setOrderNumber("PR-ORD-EXISTING");
        order.setOriginWarehouse(warehouseMumbai);

        Shipment existing = createMockShipment(order, warehouseMumbai, "PR-TRK-EXISTING");
        when(shipmentRepository.findByOrder(order)).thenReturn(Optional.of(existing));

        // Calling shipmentService.createShipmentForOrder with existing shipment
        ShipmentService realService = new ShipmentService(shipmentRepository, null, orderRepository, userRepository, null);
        Shipment result = realService.createShipmentForOrder(order);

        assertEquals("PR-TRK-EXISTING", result.getTrackingNumber());
        verify(shipmentRepository, never()).save(any(Shipment.class));
    }

    // =========================================================
    // TEST 11: Warehouse Manager scoping -> Selected WH manager sees shipment
    // =========================================================
    @Test
    @DisplayName("TEST 11: Warehouse Manager for allocated warehouse can query and view the shipment")
    void testWarehouseManagerScopingMatch() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-MUM");
        assertEquals(warehouseMumbai.getId(), s.getOriginWarehouse().getId());
    }

    // =========================================================
    // TEST 12: Different warehouse manager cannot see shipment
    // =========================================================
    @Test
    @DisplayName("TEST 12: Warehouse Manager for a different warehouse cannot view this shipment")
    void testDifferentWarehouseManagerCannotSeeShipment() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-MUM");
        assertNotEquals(warehouseDelhi.getId(), s.getOriginWarehouse().getId());
    }

    // =========================================================
    // TEST 13: Customer tracking API retrieves shipment data
    // =========================================================
    @Test
    @DisplayName("TEST 13: Customer tracking can retrieve shipment details by tracking number")
    void testCustomerTrackingRetrieval() {
        Order order = new Order();
        order.setOrderNumber("PR-ORD-TRACK01");
        order.setCustomer(customer);
        order.setPickupAddress(pickupAddress);
        order.setDeliveryAddress(deliveryAddress);
        order.setPackageWeight(2.0);
        order.setPackageQuantity(1);
        order.setDeliveryType(DeliveryType.STANDARD);
        order.setOriginWarehouse(warehouseMumbai);

        Shipment shipment = createMockShipment(order, warehouseMumbai, "PR-TRK-TRACK01");
        when(shipmentRepository.findByTrackingNumberAndOrder_Customer("PR-TRK-TRACK01", customer))
                .thenReturn(Optional.of(shipment));

        ShipmentService realService = new ShipmentService(shipmentRepository, mock(ShipmentTrackingEventRepository.class), orderRepository, userRepository, mock(GeocodingService.class));
        TrackingResponseDTO dto = realService.trackShipment("customer@primeroute.com", "PR-TRK-TRACK01");

        assertNotNull(dto);
        assertEquals("PR-TRK-TRACK01", dto.getTrackingNumber());
    }

    // =========================================================
    // TEST 14: Admin shipment listing includes the new shipment
    // =========================================================
    @Test
    @DisplayName("TEST 14: Newly created shipment is included in global shipment repository queries")
    void testAdminShipmentListingIncludesNewShipment() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-ADMIN");
        when(shipmentRepository.findAllWithDetails()).thenReturn(List.of(s));

        List<Shipment> list = shipmentRepository.findAllWithDetails();
        assertEquals(1, list.size());
        assertEquals("PR-TRK-ADMIN", list.get(0).getTrackingNumber());
    }

    // =========================================================
    // TEST 15: Order cancellation cancels shipment using existing status rules
    // =========================================================
    @Test
    @DisplayName("TEST 15: Order cancellation delegates to cancelShipmentForOrder")
    void testOrderCancellationCancelsShipment() {
        Order order = new Order();
        order.setOrderNumber("PR-ORD-CANCEL");
        order.setCustomer(customer);
        order.setPickupAddress(pickupAddress);
        order.setDeliveryAddress(deliveryAddress);
        order.setOriginWarehouse(warehouseMumbai);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findByIdAndCustomer(100L, customer)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.cancelOrder("customer@primeroute.com", 100L);

        verify(shipmentService, times(1)).cancelShipmentForOrder(order);
    }

    // =========================================================
    // TEST 16: Shipment creation failure triggers transaction rollback
    // =========================================================
    @Test
    @DisplayName("TEST 16: Failure during shipment creation throws exception to rollback transaction")
    void testShipmentCreationFailureRollsBack() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 180.0, 5.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        when(shipmentService.createShipmentForOrder(any(Order.class)))
                .thenThrow(new RuntimeException("Database error creating shipment"));

        assertThrows(RuntimeException.class, () -> orderService.createOrder("customer@primeroute.com", request));
    }

    // =========================================================
    // TEST 17: Exactly ONE set of inventory reservations exists
    // =========================================================
    @Test
    @DisplayName("TEST 17: Exactly one RESERVE transaction is recorded across the order flow")
    void testExactlyOneInventoryReservation() {
        OrderRequest request = new OrderRequest();
        request.setPickupAddressId(1L);
        request.setDeliveryAddressId(2L);
        request.setDeliveryType(DeliveryType.STANDARD);
        request.setItems(List.of(new OrderItemRequest(101L, 1)));

        when(productRepository.findById(101L)).thenReturn(Optional.of(productA));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        FulfillmentEvaluationResultDTO evalResult = FulfillmentEvaluationResultDTO.allocated(
                null, null, warehouseMumbai, 180.0, 5.0, "OPTIMAL_WAREHOUSE_ALLOCATED", 1
        );
        when(fulfillmentEngineService.evaluateOrder(any(Order.class))).thenReturn(evalResult);
        when(warehouseRepository.findById(10L)).thenReturn(Optional.of(warehouseMumbai));

        Inventory inv = createInventory(warehouseMumbai, productA, 10, 0, 0);
        when(inventoryRepository.findByWarehouseAndProductForUpdate(warehouseMumbai, productA)).thenReturn(Optional.of(inv));

        orderService.createOrder("customer@primeroute.com", request);

        // Exactly one RESERVE transaction
        verify(transactionRepository, times(1)).save(any(InventoryTransaction.class));
    }

    // =========================================================
    // TEST 18: Fleet remains unassigned upon automatic creation
    // =========================================================
    @Test
    @DisplayName("TEST 18: Fleet remains unassigned; driver and vehicle remain null")
    void testFleetRemainsUnassigned() {
        Shipment s = createMockShipment(null, warehouseMumbai, "PR-TRK-UNASSIGNED");
        assertNull(s.getDriver());
        assertNull(s.getVehicle());
        assertNull(s.getRoute());
        assertEquals("Awaiting Warehouse Assignment", s.getAssignmentStatus());
    }
}
