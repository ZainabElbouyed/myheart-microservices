package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    private String id;
    private String invoiceNumber;
    private String patientId;
    private String patientName;
    private String appointmentId;
    private Double subtotal;
    private Double taxAmount;
    private Double total;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime paidAt;
    private String paymentMethod;
    private String warning;
}