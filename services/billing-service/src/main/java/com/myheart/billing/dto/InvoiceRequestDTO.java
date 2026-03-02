package com.myheart.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceRequestDTO {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    private String patientName;
    
    private String appointmentId;
    
    @NotNull(message = "Subtotal is required")
    @Positive(message = "Subtotal must be positive")
    private BigDecimal subtotal;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be >= 0")
    private BigDecimal taxRate;
    
    private LocalDateTime dueDate;
    
    private String description;
    
    private String notes;
}