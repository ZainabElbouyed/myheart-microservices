package com.myheart.billing.dto;

import com.myheart.billing.entity.Invoice;
import com.myheart.billing.entity.Payment;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class InvoiceResponseDTO {
    
    private String id;
    private String patientId;
    private String patientName;
    private String appointmentId;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private Invoice.InvoiceStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime issuedDate;
    private LocalDateTime paidDate;
    private String description;
    private String notes;
    private List<PaymentDTO> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    public static class PaymentDTO {
        private String id;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionId;
        private String reference;
        private String status;
        private LocalDateTime paymentDate;
        
        public static PaymentDTO fromEntity(Payment payment) {
            return PaymentDTO.builder()
                    .id(payment.getId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .transactionId(payment.getTransactionId())
                    .reference(payment.getReference())
                    .status(payment.getStatus().name())
                    .paymentDate(payment.getPaymentDate())
                    .build();
        }
    }
    
    public static InvoiceResponseDTO fromEntity(Invoice invoice) {
        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .patientId(invoice.getPatientId())
                .patientName(invoice.getPatientName())
                .appointmentId(invoice.getAppointmentId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .subtotal(invoice.getSubtotal())
                .taxRate(invoice.getTaxRate())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .paidAmount(invoice.getPaidAmount())
                .remainingAmount(invoice.getRemainingAmount())
                .status(invoice.getStatus())
                .dueDate(invoice.getDueDate())
                .issuedDate(invoice.getIssuedDate())
                .paidDate(invoice.getPaidDate())
                .description(invoice.getDescription())
                .notes(invoice.getNotes())
                .payments(invoice.getPayments().stream()
                        .map(PaymentDTO::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}