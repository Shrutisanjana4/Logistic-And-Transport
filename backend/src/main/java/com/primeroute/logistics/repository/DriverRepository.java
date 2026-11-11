package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Driver;
import com.primeroute.logistics.entity.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByEmail(String email);

    Optional<Driver> findByEmailIgnoreCase(String email);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    Optional<Driver> findByLicenseNumberIgnoreCase(String licenseNumber);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByStatusOrderByFullNameAsc(DriverStatus status);

    @Query("SELECT d FROM Driver d WHERE d.status NOT IN (com.primeroute.logistics.entity.DriverStatus.INACTIVE, com.primeroute.logistics.entity.DriverStatus.OFF_DUTY) " +
           "AND NOT EXISTS (" +
           "  SELECT s FROM Shipment s WHERE s.driver = d " +
           "  AND s.status NOT IN (com.primeroute.logistics.entity.OrderStatus.DELIVERED, com.primeroute.logistics.entity.OrderStatus.CANCELLED)" +
           ") ORDER BY d.fullName ASC")
    List<Driver> findAvailableDrivers();

    List<Driver> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT d FROM Driver d LEFT JOIN FETCH d.shipments WHERE d.id = :id")
    Optional<Driver> findByIdWithShipments(Long id);

    long countByStatus(DriverStatus status);

    long countByWarehouseAndStatus(com.primeroute.logistics.entity.Warehouse warehouse, DriverStatus status);
}
