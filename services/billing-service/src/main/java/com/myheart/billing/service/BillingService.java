package com.myheart.billing.service;

import com.myheart.billing.dto.InvoiceRequestDTO;
import com.myheart.billing.dto.InvoiceResponseDTO;
import com.myheart.billing.dto.PaymentRequestDTO;
import com.myheart.billing.entity.Invoice;
import com.myheart.billing.entity.Payment;
import com.myheart.billing.exception.InvoiceNotFoundException;
import com.myheart.billing.repository.InvoiceRepository;
import com.myheart.billing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingService {
    
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO request) {
        log.info("Creating new invoice for patient: {}", request.getPatientId());
        
        Invoice invoice = new Invoice();
        invoice.setPatientId(request.getPatientId());
        invoice.setPatientName(request.getPatientName());
        invoice.setAppointmentId(request.getAppointmentId());
        invoice.setSubtotal(request.getSubtotal());
        invoice.setTaxRate(request.getTaxRate() != null ? request.getTaxRate() : BigDecimal.ZERO);
        
        // Calculer les taxes et le total
        BigDecimal taxAmount = request.getSubtotal()
                .multiply(invoice.getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotal(request.getSubtotal().add(taxAmount));
        
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setDescription(request.getDescription());
        invoice.setNotes(request.getNotes());
        invoice.setDueDate(request.getDueDate());
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created successfully with number: {}", savedInvoice.getInvoiceNumber());
        
        return InvoiceResponseDTO.fromEntity(savedInvoice);
    }
    
    public InvoiceResponseDTO getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        return InvoiceResponseDTO.fromEntity(invoice);
    }
    
    public InvoiceResponseDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with number: " + invoiceNumber));
        return InvoiceResponseDTO.fromEntity(invoice);
    }
    
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<InvoiceResponseDTO> getInvoicesByPatient(String patientId) {
        return invoiceRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(InvoiceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<InvoiceResponseDTO> getInvoicesByStatus(Invoice.InvoiceStatus status) {
        return invoiceRepository.findByStatus(status)
                .stream()
                .map(InvoiceResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    

    public InvoiceResponseDTO payInvoice(String id, String method) {
        log.info("💰 Processing payment for invoice: {} with method: {}", id, method);
        
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid");
        }
        
        // Créer le paiement
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getRemainingAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(method));
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        
        invoice.addPayment(payment);
        paymentRepository.save(payment);
        
        // Mettre à jour la facture
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("✅ Invoice {} paid successfully", id);
        
        return InvoiceResponseDTO.fromEntity(updatedInvoice);
    }
    
    @Transactional
    public InvoiceResponseDTO updateInvoiceStatus(String id, Invoice.InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        
        invoice.setStatus(status);
        if (status == Invoice.InvoiceStatus.PAID) {
            invoice.setPaidDate(LocalDateTime.now());
        }
        
        return InvoiceResponseDTO.fromEntity(invoiceRepository.save(invoice));
    }
    
    @Transactional
    public InvoiceResponseDTO cancelInvoice(String id, String reason) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + id));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid invoice");
        }
        
        invoice.setStatus(Invoice.InvoiceStatus.CANCELLED);
        invoice.setNotes((invoice.getNotes() != null ? invoice.getNotes() + " " : "") + 
                        "Cancelled: " + reason);
        
        return InvoiceResponseDTO.fromEntity(invoiceRepository.save(invoice));
    }
    
    @Transactional
    public void deleteInvoice(String id) {
        if (!invoiceRepository.existsById(id)) {
            throw new InvoiceNotFoundException("Invoice not found with id: " + id);
        }
        invoiceRepository.deleteById(id);
    }
    
    public List<Payment> getPaymentsForInvoice(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }
    
    public Map<String, Object> getInvoiceStatistics() {
        List<Object[]> results = invoiceRepository.countByStatus();
        Map<String, Long> statusCount = new HashMap<>();
        
        for (Object[] result : results) {
            Invoice.InvoiceStatus status = (Invoice.InvoiceStatus) result[0];
            Long count = (Long) result[1];
            statusCount.put(status.name(), count);
        }
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal monthlyRevenue = invoiceRepository.getTotalRevenue(startOfMonth, now);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInvoices", invoiceRepository.count());
        stats.put("byStatus", statusCount);
        stats.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);
        
        return stats;
    }
    
    private String generateInvoiceNumber() {
        String prefix = "INV";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String invoiceNumber = prefix + "-" + datePart + "-" + uniquePart;
        
        // Vérifier l'unicité
        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            invoiceNumber = prefix + "-" + datePart + "-" + uniquePart;
        }
        
        return invoiceNumber;
    }

    public InvoiceResponseDTO processPayment(String invoiceId, PaymentRequestDTO request) {
        log.info("Processing payment for invoice: {} with method: {}", invoiceId, request.getPaymentMethod());
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with id: " + invoiceId));
        
        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new IllegalStateException("Invoice is already paid");
        }
        
        BigDecimal remainingAmount = invoice.getRemainingAmount();
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds remaining balance");
        }
        
        // Créer le paiement
        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(request.getTransactionId());
        payment.setReference(request.getReference());
        payment.setNotes(request.getNotes());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        
        invoice.addPayment(payment);
        paymentRepository.save(payment);
        
        // Mettre à jour le statut de la facture
        BigDecimal newPaidAmount = invoice.getPaidAmount();
        if (newPaidAmount.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoice.setPaidDate(LocalDateTime.now());
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(Invoice.InvoiceStatus.PARTIALLY_PAID);
        }
        
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Payment processed successfully. New status: {}", updatedInvoice.getStatus());
        
        return InvoiceResponseDTO.fromEntity(updatedInvoice);
    }
}