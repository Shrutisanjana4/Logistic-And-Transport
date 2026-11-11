package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Address;
import com.primeroute.logistics.entity.Order;
import com.primeroute.logistics.entity.OrderStatus;
import com.primeroute.logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);

    List<Order> findByCustomer(User customer);

    Optional<Order> findByIdAndCustomer(Long id, User customer);

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByOrderNumberAndCustomer(String orderNumber, User customer);

    boolean existsByOrderNumber(String orderNumber);

    boolean existsByPickupAddress(Address pickupAddress);

    boolean existsByDeliveryAddress(Address deliveryAddress);

    long countByCustomer(User customer);

    long countByCustomerAndStatus(User customer, OrderStatus status);

    List<Order> findTop5ByCustomerOrderByCreatedAtDesc(User customer);

    long countByStatus(OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.status <> com.primeroute.logistics.entity.OrderStatus.CANCELLED")
    Double sumTotalRevenue();

    List<Order> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt ASC")
    List<Order> findOrdersSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.customer = :customer AND o.status <> com.primeroute.logistics.entity.OrderStatus.CANCELLED")
    Double sumTotalSpentByCustomer(@Param("customer") User customer);

    List<Order> findTop10ByCustomerOrderByCreatedAtDesc(User customer);

    @Query("SELECT o FROM Order o WHERE o.status IN (com.primeroute.logistics.entity.OrderStatus.PENDING, com.primeroute.logistics.entity.OrderStatus.CONFIRMED, com.primeroute.logistics.entity.OrderStatus.PICKED_UP) AND NOT EXISTS (SELECT s FROM Shipment s WHERE s.order = o) ORDER BY o.createdAt DESC")
    List<Order> findEligibleOrdersForShipment();

    List<Order> findByOriginWarehouseOrderByCreatedAtDesc(com.primeroute.logistics.entity.Warehouse originWarehouse);

    List<Order> findByOriginWarehouse(com.primeroute.logistics.entity.Warehouse originWarehouse);

    java.util.Optional<Order> findByIdAndOriginWarehouse(Long id, com.primeroute.logistics.entity.Warehouse originWarehouse);

}
