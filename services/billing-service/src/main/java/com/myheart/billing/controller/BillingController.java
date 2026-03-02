package com.myheart.billing.controller;

import com.myheart.billing.dto.InvoiceRequestDTO;
import com.myheart.billing.dto.InvoiceResponseDTO;
import com.myheart.billing.dto.PaymentRequestDTO;
import com.myheart.billing.entity.Invoice;
import com.myheart.billing.entity.Payment;
import com.myheart.billing.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {
    
    private final BillingService billingService;
    
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody InvoiceRequestDTO request) {
        InvoiceResponseDTO response = billingService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> getAllInvoices() {
        List<InvoiceResponseDTO> invoices = billingService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable String id) {
        InvoiceResponseDTO response = billingService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/invoices/number/{invoiceNumber}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceResponseDTO response = billingService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/invoices/patient/{patientId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesByPatient(@PathVariable String patientId) {
        List<InvoiceResponseDTO> invoices = billingService.getInvoicesByPatient(patientId);
        return ResponseEntity.ok(invoices);
    }
    
    @GetMapping("/invoices/status/{status}")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesByStatus(@PathVariable Invoice.InvoiceStatus status) {
        List<InvoiceResponseDTO> invoices = billingService.getInvoicesByStatus(status);
        return ResponseEntity.ok(invoices);
    }
    
    @PostMapping("/invoices/{id}/payments")
    public ResponseEntity<InvoiceResponseDTO> processPayment(
            @PathVariable String id,
            @Valid @RequestBody PaymentRequestDTO request) {
        InvoiceResponseDTO response = billingService.processPayment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/invoices/{id}/payments")
    public ResponseEntity<List<Payment>> getPaymentsForInvoice(@PathVariable String id) {
        List<Payment> payments = billingService.getPaymentsForInvoice(id);
        return ResponseEntity.ok(payments);
    }
    
    @PatchMapping("/invoices/{id}/status")
    public ResponseEntity<InvoiceResponseDTO> updateInvoiceStatus(
            @PathVariable String id,
            @RequestParam Invoice.InvoiceStatus status) {
        InvoiceResponseDTO response = billingService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/invoices/{id}/cancel")
    public ResponseEntity<InvoiceResponseDTO> cancelInvoice(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {
        InvoiceResponseDTO response = billingService.cancelInvoice(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/invoices/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable String id) {
        billingService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = billingService.getInvoiceStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Billing service is running");
    }
}