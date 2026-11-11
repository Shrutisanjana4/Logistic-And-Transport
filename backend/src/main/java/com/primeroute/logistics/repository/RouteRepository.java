package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Driver;
import com.primeroute.logistics.entity.Route;
import com.primeroute.logistics.entity.RouteStatus;
import com.primeroute.logistics.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteCode(String routeCode);

    Optional<Route> findByRouteCodeIgnoreCase(String routeCode);

    boolean existsByRouteCodeIgnoreCase(String routeCode);

    boolean existsByRouteCodeIgnoreCaseAndIdNot(String routeCode, Long id);

    List<Route> findByStatus(RouteStatus status);

    List<Route> findByDriver(Driver driver);

    List<Route> findByVehicle(Vehicle vehicle);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Route r WHERE r.driver.id = :driverId AND r.status = :status")
    boolean existsByDriverIdAndStatus(@Param("driverId") Long driverId, @Param("status") RouteStatus status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Route r WHERE r.driver.id = :driverId AND r.status = :status AND r.id <> :id")
    boolean existsByDriverIdAndStatusAndIdNot(@Param("driverId") Long driverId, @Param("status") RouteStatus status, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Route r WHERE r.vehicle.id = :vehicleId AND r.status = :status")
    boolean existsByVehicleIdAndStatus(@Param("vehicleId") Long vehicleId, @Param("status") RouteStatus status);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Route r WHERE r.vehicle.id = :vehicleId AND r.status = :status AND r.id <> :id")
    boolean existsByVehicleIdAndStatusAndIdNot(@Param("vehicleId") Long vehicleId, @Param("status") RouteStatus status, @Param("id") Long id);

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.driver LEFT JOIN FETCH r.vehicle ORDER BY r.createdAt DESC")
    List<Route> findAllWithDetails();
}
