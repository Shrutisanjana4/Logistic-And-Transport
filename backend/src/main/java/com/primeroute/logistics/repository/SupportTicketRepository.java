package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.SupportTicket;
import com.primeroute.logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByCustomerOrderByCreatedAtDesc(User customer);

    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    Optional<SupportTicket> findByIdAndCustomer(Long id, User customer);

    boolean existsByTicketNumber(String ticketNumber);
}