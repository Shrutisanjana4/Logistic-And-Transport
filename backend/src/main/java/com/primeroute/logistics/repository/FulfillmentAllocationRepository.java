package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.FulfillmentAllocation;
import com.primeroute.logistics.entity.FulfillmentAllocationStatus;
import com.primeroute.logistics.entity.Order;
import com.primeroute.logistics.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FulfillmentAllocationRepository extends JpaRepository<FulfillmentAllocation, Long> {

    List<FulfillmentAllocation> findByOrder(Order order);

    List<FulfillmentAllocation> findByOrderId(Long orderId);

    List<FulfillmentAllocation> findByWarehouse(Warehouse warehouse);

    List<FulfillmentAllocation> findByAllocationStatus(FulfillmentAllocationStatus allocationStatus);

    List<FulfillmentAllocation> findByOrderOrderByAllocatedAtDesc(Order order);

    List<FulfillmentAllocation> findByOrderIdOrderByAllocatedAtDesc(Long orderId);

    Optional<FulfillmentAllocation> findTopByOrderOrderByAllocatedAtDesc(Order order);

    Optional<FulfillmentAllocation> findTopByOrderIdOrderByAllocatedAtDesc(Long orderId);
}
