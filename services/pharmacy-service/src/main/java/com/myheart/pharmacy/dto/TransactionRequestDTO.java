package com.myheart.pharmacy.dto;

import com.myheart.pharmacy.entity.InventoryTransaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionRequestDTO {
    
    @NotBlank(message = "Medicine ID is required")
    private String medicineId;
    
    @NotNull(message = "Transaction type is required")
    private InventoryTransaction.TransactionType type;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    private String reference;
    
    private String patientId;
    
    private String doctorId;
    
    private String prescriptionId;
    
    private String notes;
    
    private String createdBy;
}