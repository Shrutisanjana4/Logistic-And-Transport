package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Shipment;
import com.primeroute.logistics.entity.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Long> {

    List<ShipmentTrackingEvent> findByShipmentOrderByEventTimeAsc(Shipment shipment);
}
