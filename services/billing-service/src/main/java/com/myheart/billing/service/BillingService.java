package com.myheart.billing.service;

import com.myheart.billing.client.PatientServiceClient;
import com.myheart.billing.client.AppointmentServiceClient;
import com.myheart.billing.dto.InvoiceRequestDTO;
import com.myheart.billing.dto.InvoiceResponseDTO;
import com.myheart.billing.dto.PaymentRequestDTO;
import com.myheart.billing.entity.Invoice;
import com.myheart.billing.entity.Payment;
import com.myheart.billing.exception.InvoiceNotFoundException;
import com.myheart.billing.repository.InvoiceRepository;
import com.myheart.billing.repository.PaymentRepository;
import com.myheart.common.dto.AppointmentDTO;
import com.myheart.common.dto.PatientDTO;
import com.myheart.common.dto.InvoiceRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    
    // ========== CLIENTS FEIGN AVEC CIRCUIT BREAKER ==========
    private final PatientServiceClient patientClient;
    private final AppointmentServiceClient appointmentClient;
    
    // ========== MÉTHODE PRINCIPALE EXISTANTE ==========
    
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
    
    // ========== MÉTHODES AVEC CIRCUIT BREAKER POUR LES APPELS EXTERNES ==========
    
    /**
     * Récupère les détails d'un rendez-vous pour facturation
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "getAppointmentFallback")
    public AppointmentDTO getAppointmentDetails(String appointmentId) {
        log.info("Appel à appointment-service pour récupérer les détails du rendez-vous: {}", appointmentId);
        return appointmentClient.getAppointmentById(appointmentId);
    }
    
    /**
     * Fallback pour getAppointmentDetails
     */
    public AppointmentDTO getAppointmentFallback(String appointmentId, Exception e) {
        log.error("Fallback pour getAppointmentDetails - appointment-service indisponible: {}", e.getMessage());
        return AppointmentDTO.builder()
            .id(appointmentId)
            .patientId("INCONNU")
            .doctorId("INCONNU")
            .status("UNKNOWN")
            .build();
    }
    
    /**
     * Récupère les détails d'un patient pour facturation
     */
    @CircuitBreaker(name = "patientService", fallbackMethod = "getPatientFallback")
    public PatientDTO getPatientDetails(String patientId) {
        log.info("Appel à patient-service pour récupérer les détails du patient: {}", patientId);
        return patientClient.getPatientById(patientId);
    }
    
    /**
     * Fallback pour getPatientDetails
     */
    public PatientDTO getPatientFallback(String patientId, Exception e) {
        log.error("Fallback pour getPatientDetails - patient-service indisponible: {}", e.getMessage());
        return PatientDTO.builder()
            .id(patientId)
            .firstName("Patient")
            .lastName("Indisponible")
            .email("inconnu@fallback.com")
            .build();
    }
    
    /**
     * Crée une facture à partir d'un rendez-vous (méthode unifiée)
     * Note: InvoiceRequest utilise des Double, donc conversion en BigDecimal nécessaire
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "createInvoiceFromAppointmentFallback")
    public InvoiceResponseDTO createInvoiceFromAppointment(InvoiceRequest request) {
        log.info("Création facture pour appointment: {}", request.getAppointmentId());
        
        // Récupérer les détails du rendez-vous
        AppointmentDTO appointment = appointmentClient.getAppointmentById(request.getAppointmentId());
        
        // Récupérer les détails du patient
        PatientDTO patient = patientClient.getPatientById(appointment.getPatientId());
        
        // Créer la facture
        Invoice invoice = new Invoice();
        invoice.setAppointmentId(appointment.getId());
        invoice.setPatientId(patient.getId());
        invoice.setPatientName(patient.getFirstName() + " " + patient.getLastName());
        
        // ✅ CORRECTION: Convertir Double en BigDecimal
        Double subtotalDouble = request.getSubtotal();
        BigDecimal subtotal = subtotalDouble != null ? 
            BigDecimal.valueOf(subtotalDouble) : BigDecimal.valueOf(50.0);
        invoice.setSubtotal(subtotal);
        
        Double taxRateDouble = request.getTaxRate();
        BigDecimal taxRate = taxRateDouble != null ? 
            BigDecimal.valueOf(taxRateDouble) : BigDecimal.valueOf(20.0);
        invoice.setTaxRate(taxRate);
        
        // Calculer taxes et total
        BigDecimal taxAmount = invoice.getSubtotal()
                .multiply(invoice.getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotal(invoice.getSubtotal().add(taxAmount));
        
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setDescription(request.getDescription() != null ? request.getDescription() : 
                "Consultation médicale");
        invoice.setDueDate(LocalDateTime.now().plusDays(30));
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Facture créée avec succès: {}", savedInvoice.getInvoiceNumber());
        
        return InvoiceResponseDTO.fromEntity(savedInvoice);
    }
    
    /**
     * Fallback pour createInvoiceFromAppointment
     */
    public InvoiceResponseDTO createInvoiceFromAppointmentFallback(InvoiceRequest request, Exception e) {
        log.error("Fallback pour createInvoiceFromAppointment - service(s) indisponible(s): {}", e.getMessage());
        
        Invoice invoice = new Invoice();
        invoice.setAppointmentId(request.getAppointmentId());
        invoice.setPatientId("INCONNU");
        invoice.setPatientName("Patient Indisponible");
        
        // ✅ CORRECTION: Même conversion Double → BigDecimal
        Double subtotalDouble = request.getSubtotal();
        BigDecimal subtotal = subtotalDouble != null ? 
            BigDecimal.valueOf(subtotalDouble) : BigDecimal.valueOf(50.0);
        invoice.setSubtotal(subtotal);
        
        Double taxRateDouble = request.getTaxRate();
        BigDecimal taxRate = taxRateDouble != null ? 
            BigDecimal.valueOf(taxRateDouble) : BigDecimal.valueOf(20.0);
        invoice.setTaxRate(taxRate);
        
        BigDecimal taxAmount = invoice.getSubtotal()
                .multiply(invoice.getTaxRate())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotal(invoice.getSubtotal().add(taxAmount));
        
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setDescription(request.getDescription() != null ? request.getDescription() : 
                "Consultation (mode dégradé)");
        invoice.setDueDate(LocalDateTime.now().plusDays(30));
        invoice.setIssuedDate(LocalDateTime.now());
        invoice.setStatus(Invoice.InvoiceStatus.PENDING);
        invoice.setNotes("Créé en mode dégradé - informations patient non vérifiées");
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return InvoiceResponseDTO.fromEntity(savedInvoice);
    }
    
    // ========== MÉTHODES EXISTANTES ==========
    
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
}