package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.InventoryTransaction;
import com.primeroute.logistics.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByWarehouseOrderByTimestampDesc(Warehouse warehouse);

    List<InventoryTransaction> findTop20ByWarehouseOrderByTimestampDesc(Warehouse warehouse);

    List<InventoryTransaction> findByWarehouseAndSkuOrderByTimestampDesc(Warehouse warehouse, String sku);

    @Query("SELECT t FROM InventoryTransaction t WHERE t.warehouse = :warehouse " +
           "AND (:sku IS NULL OR LOWER(t.sku) LIKE LOWER(CONCAT('%', :sku, '%'))) " +
           "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
           "AND (:endDate IS NULL OR t.timestamp <= :endDate) " +
           "ORDER BY t.timestamp DESC")
    List<InventoryTransaction> filterTransactions(
            @Param("warehouse") Warehouse warehouse,
            @Param("sku") String sku,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
