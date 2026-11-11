package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.CreateTicketRequest;
import com.primeroute.logistics.dto.SupportContactDTO;
import com.primeroute.logistics.dto.SupportTicketDTO;
import com.primeroute.logistics.dto.SupportTicketMessageDTO;
import com.primeroute.logistics.entity.SupportContact;
import com.primeroute.logistics.entity.SupportTicket;
import com.primeroute.logistics.entity.SupportTicketMessage;
import com.primeroute.logistics.entity.User;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.SupportContactRepository;
import com.primeroute.logistics.repository.SupportTicketMessageRepository;
import com.primeroute.logistics.repository.SupportTicketRepository;
import com.primeroute.logistics.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketService {

    private static final Logger log = LoggerFactory.getLogger(SupportTicketService.class);
    private final SecureRandom secureRandom = new SecureRandom();

    private final SupportTicketRepository supportTicketRepository;
    private final SupportTicketMessageRepository messageRepository;
    private final SupportContactRepository supportContactRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Value("${app.support.email:support@primeroute.com}")
    private String defaultSupportEmail;

    @Value("${app.support.phone:+91 98765 43210}")
    private String defaultSupportPhone;

    @Value("${app.support.availability:24/7 Dedicated Logistics Assistance}")
    private String defaultSupportAvailability;

    @Value("${app.support.description:Operations desk hotline for critical freight inquiries and routing delays.}")
    private String defaultSupportDescription;

    public SupportTicketService(
            SupportTicketRepository supportTicketRepository,
            SupportTicketMessageRepository messageRepository,
            SupportContactRepository supportContactRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.supportTicketRepository = supportTicketRepository;
        this.messageRepository = messageRepository;
        this.supportContactRepository = supportContactRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public SupportContactDTO getSupportContact() {
        return supportContactRepository.findAll().stream().findFirst()
                .map(contact -> new SupportContactDTO(
                        contact.getSupportEmail(),
                        contact.getSupportPhone(),
                        contact.getAvailability(),
                        contact.getDescription()
                ))
                .orElseGet(() -> new SupportContactDTO(
                        defaultSupportEmail,
                        defaultSupportPhone,
                        defaultSupportAvailability,
                        defaultSupportDescription
                ));
    }

    @Transactional
    public SupportTicketDTO createTicket(String email, CreateTicketRequest request) {
        User customer = getCustomer(email);

        String ticketNumber = generateTicketNumber();

        SupportTicket ticket = new SupportTicket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setCustomer(customer);
        ticket.setSubject(request.getSubject().trim());

        String category = request.getCategory() != null && !request.getCategory().isBlank()
                ? request.getCategory().trim().toUpperCase()
                : "OTHER";
        ticket.setCategory(category);

        String priority = request.getPriority() != null && !request.getPriority().isBlank()
                ? request.getPriority().trim().toUpperCase()
                : "MEDIUM";
        ticket.setPriority(priority);

        ticket.setStatus("OPEN");
        ticket.setDescription(request.getDescription().trim());
        ticket.setOrderNumber(request.getOrderNumber() != null && !request.getOrderNumber().isBlank()
                ? request.getOrderNumber().trim() : null);
        ticket.setTrackingNumber(request.getTrackingNumber() != null && !request.getTrackingNumber().isBlank()
                ? request.getTrackingNumber().trim() : null);

        LocalDateTime now = LocalDateTime.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);

        SupportTicket savedTicket = supportTicketRepository.save(ticket);

        try {
            notificationService.createNotification(
                    customer,
                    "Support Ticket Opened",
                    "Support ticket " + savedTicket.getTicketNumber() + " has been created.",
                    "SUPPORT"
            );
            String customerDisplay = customer.getFullName() != null && !customer.getFullName().isBlank()
                    ? customer.getFullName() : customer.getEmail();
            notificationService.notifyAdmins(
                    "New Customer Support Ticket",
                    "Ticket #" + savedTicket.getTicketNumber() + " opened by " + customerDisplay + ": " + savedTicket.getSubject(),
                    "SUPPORT_TICKET"
            );
        } catch (Exception ex) {
            log.warn("Failed to create support ticket notification: {}", ex.getMessage());
        }

        return convertToDTO(savedTicket);
    }

    public List<SupportTicketDTO> getCustomerTickets(String email) {
        User customer = getCustomer(email);
        return supportTicketRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public SupportTicketDTO getTicketById(String email, Long id) {
        User customer = getCustomer(email);
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomer(id, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));
        return convertToDTO(ticket);
    }

    // --- Message handling for Customer ---
    @Transactional(readOnly = true)
    public List<SupportTicketMessageDTO> getCustomerMessages(String email, Long ticketId) {
        User customer = getCustomer(email);
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomer(ticketId, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));
        return messageRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(this::convertMessageToDTO)
                .toList();
    }

    @Transactional
    public SupportTicketMessageDTO addCustomerMessage(String email, Long ticketId, String text) {
        User customer = getCustomer(email);
        SupportTicket ticket = supportTicketRepository.findByIdAndCustomer(ticketId, customer)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));

        String senderName = customer.getFullName() != null && !customer.getFullName().isBlank()
                ? customer.getFullName()
                : "Customer";

        SupportTicketMessage message = new SupportTicketMessage(
                ticket,
                customer,
                "CUSTOMER",
                senderName,
                text.trim(),
                LocalDateTime.now()
        );

        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        SupportTicketMessage saved = messageRepository.save(message);

        try {
            String snippet = text.trim();
            if (snippet.length() > 50) {
                snippet = snippet.substring(0, 47) + "...";
            }
            notificationService.notifyAdmins(
                    "Support Ticket Response",
                    "Customer replied on ticket " + ticket.getTicketNumber() + ": " + snippet,
                    "SUPPORT_TICKET"
            );
        } catch (Exception ex) {
            log.warn("Failed to notify admins of customer message: {}", ex.getMessage());
        }

        return convertMessageToDTO(saved);
    }

    // --- Admin Operations ---
    public List<SupportTicketDTO> getAllTicketsForAdmin() {
        return supportTicketRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public SupportTicketDTO getTicketByIdForAdmin(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));
        return convertToDTO(ticket);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketMessageDTO> getAdminMessages(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));
        return messageRepository.findByTicketOrderByCreatedAtAsc(ticket).stream()
                .map(this::convertMessageToDTO)
                .toList();
    }

    @Transactional
    public SupportTicketMessageDTO addAdminMessage(String email, Long ticketId, String text) {
        User adminUser = getCustomer(email);
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));

        String senderName = adminUser.getFullName() != null && !adminUser.getFullName().isBlank()
                ? adminUser.getFullName()
                : "PrimeRoute Operations";

        SupportTicketMessage message = new SupportTicketMessage(
                ticket,
                adminUser,
                "ADMIN",
                senderName,
                text.trim(),
                LocalDateTime.now()
        );

        ticket.setUpdatedAt(LocalDateTime.now());
        supportTicketRepository.save(ticket);

        SupportTicketMessage saved = messageRepository.save(message);

        // Notify customer
        try {
            notificationService.createNotification(
                    ticket.getCustomer(),
                    "Support Team Replied",
                    "Support team replied to ticket " + ticket.getTicketNumber() + ".",
                    "SUPPORT"
            );
        } catch (Exception ex) {
            log.warn("Failed to create admin reply notification: {}", ex.getMessage());
        }

        return convertMessageToDTO(saved);
    }

    @Transactional
    public SupportTicketDTO updateTicketStatus(Long id, String status) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));

        String normalizedStatus = status.trim().toUpperCase();
        ticket.setStatus(normalizedStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        SupportTicket updated = supportTicketRepository.save(ticket);

        try {
            notificationService.createNotification(
                    ticket.getCustomer(),
                    "Ticket Status Changed",
                    "Ticket " + ticket.getTicketNumber() + " status changed to " + normalizedStatus + ".",
                    "SUPPORT"
            );
        } catch (Exception ex) {
            log.warn("Failed to create status change notification: {}", ex.getMessage());
        }

        return convertToDTO(updated);
    }

    @Transactional
    public SupportTicketDTO updateTicketPriority(Long id, String priority) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));

        String normalizedPriority = priority.trim().toUpperCase();
        ticket.setPriority(normalizedPriority);
        ticket.setUpdatedAt(LocalDateTime.now());
        SupportTicket updated = supportTicketRepository.save(ticket);

        return convertToDTO(updated);
    }

    private User getCustomer(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private String generateTicketNumber() {
        String ticketNumber;
        do {
            int randomNum = 10000000 + secureRandom.nextInt(90000000);
            ticketNumber = "PR-TKT-" + randomNum;
        } while (supportTicketRepository.existsByTicketNumber(ticketNumber));
        return ticketNumber;
    }

    private SupportTicketDTO convertToDTO(SupportTicket ticket) {
        User customer = ticket.getCustomer();
        String customerName = customer != null ? customer.getFullName() : null;
        String customerEmail = customer != null ? customer.getEmail() : null;
        String customerPhone = customer != null ? customer.getPhone() : null;

        return new SupportTicketDTO(
                ticket.getId(),
                ticket.getTicketNumber(),
                ticket.getSubject(),
                ticket.getCategory(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getDescription(),
                ticket.getOrderNumber(),
                ticket.getTrackingNumber(),
                customerName,
                customerEmail,
                customerPhone,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

    private SupportTicketMessageDTO convertMessageToDTO(SupportTicketMessage msg) {
        return new SupportTicketMessageDTO(
                msg.getId(),
                msg.getTicket().getId(),
                msg.getSenderName(),
                msg.getSenderRole(),
                msg.getMessage(),
                msg.getCreatedAt()
        );
    }
}