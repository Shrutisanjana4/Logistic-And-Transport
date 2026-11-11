package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Driver;
import com.primeroute.logistics.entity.Vehicle;
import com.primeroute.logistics.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    List<Vehicle> findAllByOrderByCreatedAtDesc();

    @Query("SELECT v FROM Vehicle v WHERE v.status NOT IN (com.primeroute.logistics.entity.VehicleStatus.INACTIVE, com.primeroute.logistics.entity.VehicleStatus.MAINTENANCE) " +
           "AND v.assignedDriver IS NOT NULL " +
           "AND NOT EXISTS (" +
           "  SELECT s FROM Shipment s WHERE s.vehicle = v " +
           "  AND s.status NOT IN (com.primeroute.logistics.entity.OrderStatus.DELIVERED, com.primeroute.logistics.entity.OrderStatus.CANCELLED)" +
           ") ORDER BY v.createdAt DESC")
    List<Vehicle> findAvailableVehicles();

    List<Vehicle> findByAssignedDriver(Driver driver);

    @Query("SELECT v FROM Vehicle v WHERE v.assignedDriver = :driver AND v.id <> :id")
    List<Vehicle> findByAssignedDriverAndIdNot(@Param("driver") Driver driver, @Param("id") Long id);

    long countByStatus(VehicleStatus status);

    long countByWarehouseAndStatus(com.primeroute.logistics.entity.Warehouse warehouse, VehicleStatus status);
}