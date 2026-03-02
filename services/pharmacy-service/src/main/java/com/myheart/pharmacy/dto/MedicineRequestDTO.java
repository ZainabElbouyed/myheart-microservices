package com.myheart.pharmacy.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicineRequestDTO {
    
    @NotBlank(message = "Medicine name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    private String genericName;
    
    private String manufacturer;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String form;
    
    private String strength;
    
    private String dosage;
    
    @Min(value = 0, message = "Initial stock cannot be negative")
    private Integer initialStock;
    
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;
    
    @Min(value = 1, message = "Maximum stock must be at least 1")
    private Integer maximumStock;
    
    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;
    
    @Positive(message = "Selling price must be positive")
    private BigDecimal sellingPrice;
    
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
    
    private String batchNumber;
    
    private String location;
    
    private Boolean requiresPrescription;
    
    private String prescriptionDetails;
    
    private String sideEffects;
    
    private String contraindications;
    
    private String storageConditions;
    
    private String description;
}