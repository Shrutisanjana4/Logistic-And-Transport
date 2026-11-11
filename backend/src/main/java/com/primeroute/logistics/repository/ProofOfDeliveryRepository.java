package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.ProofOfDelivery;
import com.primeroute.logistics.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProofOfDeliveryRepository extends JpaRepository<ProofOfDelivery, Long> {
    Optional<ProofOfDelivery> findByShipment(Shipment shipment);
    Optional<ProofOfDelivery> findByShipmentId(Long shipmentId);
    boolean existsByShipment(Shipment shipment);
}