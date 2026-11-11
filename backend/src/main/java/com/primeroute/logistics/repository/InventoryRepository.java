package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Inventory;
import com.primeroute.logistics.entity.Product;
import com.primeroute.logistics.entity.Warehouse;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByWarehouse(Warehouse warehouse);

    List<Inventory> findByWarehouseOrderBySkuAsc(Warehouse warehouse);

    Optional<Inventory> findByWarehouseAndSku(Warehouse warehouse, String sku);

    Optional<Inventory> findByWarehouseAndProduct(Warehouse warehouse, Product product);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.warehouse = :warehouse AND i.product = :product")
    Optional<Inventory> findByWarehouseAndProductForUpdate(@Param("warehouse") Warehouse warehouse, @Param("product") Product product);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.warehouse = :warehouse AND i.sku = :sku")
    Optional<Inventory> findByWarehouseAndSkuForUpdate(@Param("warehouse") Warehouse warehouse, @Param("sku") String sku);

    List<Inventory> findByProduct(Product product);

    @Query("SELECT i FROM Inventory i WHERE i.product = :product AND i.availableStock >= :minQty AND i.warehouse.status = 'ACTIVE' ORDER BY i.availableStock DESC")
    List<Inventory> findAvailableByProduct(@Param("product") Product product, @Param("minQty") Integer minQty);

    Optional<Inventory> findByIdAndWarehouse(Long id, Warehouse warehouse);

    @Query("SELECT i FROM Inventory i WHERE i.warehouse = :warehouse AND i.availableStock <= i.reorderLevel")
    List<Inventory> findLowStockByWarehouse(@Param("warehouse") Warehouse warehouse);

    @Query("SELECT COALESCE(SUM(i.totalStock), 0) FROM Inventory i WHERE i.warehouse = :warehouse")
    Long sumTotalStockByWarehouse(@Param("warehouse") Warehouse warehouse);

    @Query("SELECT COALESCE(SUM(i.availableStock), 0) FROM Inventory i WHERE i.warehouse = :warehouse")
    Long sumAvailableStockByWarehouse(@Param("warehouse") Warehouse warehouse);

    @Query("SELECT COALESCE(SUM(i.reservedStock), 0) FROM Inventory i WHERE i.warehouse = :warehouse")
    Long sumReservedStockByWarehouse(@Param("warehouse") Warehouse warehouse);

    @Query("SELECT COALESCE(SUM(i.damagedStock), 0) FROM Inventory i WHERE i.warehouse = :warehouse")
    Long sumDamagedStockByWarehouse(@Param("warehouse") Warehouse warehouse);

    long countByWarehouse(Warehouse warehouse);
}
