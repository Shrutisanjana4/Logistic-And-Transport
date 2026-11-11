package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.entity.Invoice;
import com.primeroute.logistics.entity.Order;
import com.primeroute.logistics.repository.OrderRepository;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.InvoiceRepository;
import com.primeroute.logistics.service.InvoicePdfService;
import com.primeroute.logistics.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/invoices")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final InvoicePdfService invoicePdfService;
    private final OrderRepository orderRepository;

    public AdminInvoiceController(InvoiceService invoiceService,
                                  InvoiceRepository invoiceRepository,
                                  InvoicePdfService invoicePdfService,
                                  OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.invoiceService = invoiceService;
        this.invoiceRepository = invoiceRepository;
        this.invoicePdfService = invoicePdfService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminInvoiceResponseDTO>>> getAllInvoices() {
        List<AdminInvoiceResponseDTO> list = invoiceService.getAllInvoices();
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoices retrieved successfully", list));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InvoiceSummaryDTO>> getInvoiceSummary() {
        InvoiceSummaryDTO summary = invoiceService.getInvoiceSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice summary retrieved successfully", summary));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminInvoiceResponseDTO>> getInvoiceById(@PathVariable Long id) {
        AdminInvoiceResponseDTO dto = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice retrieved successfully", dto));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable("id") String id) {
        String cleanId = id.trim();
        Invoice invoice = null;

        try {
            Long invId = Long.parseLong(cleanId);
            invoice = invoiceRepository.findById(invId).orElse(null);
        } catch (NumberFormatException ignored) {}

        if (invoice == null) {
            invoice = invoiceRepository.findByInvoiceNumber(cleanId).orElse(null);
        }

        if (invoice == null) {
            String orderNum = cleanId.startsWith("INV-") ? cleanId.substring(4) : cleanId;
            Order order = orderRepository.findByOrderNumber(orderNum).orElse(null);
            if (order != null) {
                invoice = invoiceService.getOrCreateInvoiceForOrder(order);
            }
        }

        if (invoice == null) {
            throw new ResourceNotFoundException("Invoice not found for id: " + id);
        }

        byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);

        String filename = "PrimeRoute-" + (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : ("INV-" + invoice.getId())) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminInvoiceResponseDTO>> createInvoice(@Valid @RequestBody CreateInvoiceRequest req) {
        AdminInvoiceResponseDTO created = invoiceService.createInvoice(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Invoice created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminInvoiceResponseDTO>> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvoiceRequest req) {
        AdminInvoiceResponseDTO updated = invoiceService.updateInvoice(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminInvoiceResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateInvoiceStatusRequest req) {
        AdminInvoiceResponseDTO updated = invoiceService.updateStatus(id, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrCancelInvoice(@PathVariable Long id) {
        invoiceService.deleteOrCancelInvoice(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Invoice cancelled / deleted successfully", null));
    }
}
