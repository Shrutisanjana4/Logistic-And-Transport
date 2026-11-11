package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Warehouse;
import com.primeroute.logistics.entity.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByWarehouseCodeIgnoreCase(String warehouseCode);

    Optional<Warehouse> findByWarehouseCode(String warehouseCode);

    boolean existsByWarehouseCodeIgnoreCase(String warehouseCode);

    boolean existsByWarehouseCodeIgnoreCaseAndIdNot(String warehouseCode, Long id);

    List<Warehouse> findByStatus(WarehouseStatus status);

    long countByStatus(WarehouseStatus status);

    List<Warehouse> findAllByOrderByCreatedAtDesc();
}
