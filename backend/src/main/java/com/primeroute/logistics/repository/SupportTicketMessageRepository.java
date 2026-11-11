package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.SupportTicket;
import com.primeroute.logistics.entity.SupportTicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketMessageRepository extends JpaRepository<SupportTicketMessage, Long> {
    List<SupportTicketMessage> findByTicketOrderByCreatedAtAsc(SupportTicket ticket);
}