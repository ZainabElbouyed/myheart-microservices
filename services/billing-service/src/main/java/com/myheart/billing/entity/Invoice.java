package com.myheart.billing.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String patientId;
    
    private String patientName;
    
    private String appointmentId;
    
    @Column(unique = true, nullable = false)
    private String invoiceNumber;
    
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    private BigDecimal taxRate;
    
    private BigDecimal taxAmount;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.PENDING;
    
    private LocalDateTime dueDate;
    
    private LocalDateTime issuedDate;
    
    private LocalDateTime paidDate;
    
    private String description;
    
    private String notes;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime paidAt;
    
    public enum InvoiceStatus {
        DRAFT, PENDING, PARTIALLY_PAID, PAID, OVERDUE, CANCELLED, REFUNDED
    }
    
    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setInvoice(this);
    }
    
    public BigDecimal getPaidAmount() {
        return payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getRemainingAmount() {
        return total.subtract(getPaidAmount());
    }
}