package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Invoice;
import com.primeroute.logistics.entity.InvoiceStatus;
import com.primeroute.logistics.entity.Order;
import com.primeroute.logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT DISTINCT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "LEFT JOIN FETCH i.order o " +
           "LEFT JOIN FETCH o.pickupAddress pa " +
           "LEFT JOIN FETCH o.deliveryAddress da " +
           "LEFT JOIN FETCH i.shipment s " +
           "ORDER BY i.createdAt DESC")
    List<Invoice> findAllWithDetails();

    @Query("SELECT i FROM Invoice i " +
           "LEFT JOIN FETCH i.customer c " +
           "LEFT JOIN FETCH i.order o " +
           "LEFT JOIN FETCH o.pickupAddress pa " +
           "LEFT JOIN FETCH o.deliveryAddress da " +
           "LEFT JOIN FETCH i.shipment s " +
           "WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerOrderByCreatedAtDesc(User customer);

    Optional<Invoice> findByOrder(Order order);

    Optional<Invoice> findByOrderId(Long orderId);

    boolean existsByOrder(Order order);

    boolean existsByOrderId(Long orderId);

    List<Invoice> findByInvoiceStatus(InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i")
    long countTotalInvoices();

    @Query("SELECT COALESCE(SUM(i.totalAmount), 0.0) FROM Invoice i WHERE i.invoiceStatus <> com.primeroute.logistics.entity.InvoiceStatus.CANCELLED")
    Double sumTotalBilled();

    @Query("SELECT COALESCE(SUM(i.amountPaid), 0.0) FROM Invoice i WHERE i.invoiceStatus <> com.primeroute.logistics.entity.InvoiceStatus.CANCELLED")
    Double sumPaidAmount();

    @Query("SELECT COALESCE(SUM(i.balanceDue), 0.0) FROM Invoice i WHERE i.invoiceStatus <> com.primeroute.logistics.entity.InvoiceStatus.CANCELLED AND i.balanceDue > 0.0")
    Double sumOutstandingAmount();

    @Query("SELECT COALESCE(SUM(i.balanceDue), 0.0) FROM Invoice i WHERE i.invoiceStatus <> com.primeroute.logistics.entity.InvoiceStatus.CANCELLED AND i.balanceDue > 0.0 AND i.dueDate < CURRENT_DATE")
    Double sumOverdueAmount();

    @Query("SELECT i.invoiceNumber FROM Invoice i ORDER BY i.id DESC")
    List<String> findRecentInvoiceNumbers();
}
