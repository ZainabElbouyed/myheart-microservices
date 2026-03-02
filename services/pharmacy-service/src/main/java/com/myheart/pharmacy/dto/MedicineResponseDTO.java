package com.myheart.pharmacy.dto;

import com.myheart.pharmacy.entity.Medicine;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MedicineResponseDTO {
    
    private String id;
    private String name;
    private String genericName;
    private String manufacturer;
    private String category;
    private String form;
    private String strength;
    private String dosage;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Integer maximumStock;
    private BigDecimal unitPrice;
    private BigDecimal sellingPrice;
    private LocalDate expiryDate;
    private String batchNumber;
    private String location;
    private Boolean requiresPrescription;
    private String prescriptionDetails;
    private String sideEffects;
    private String contraindications;
    private String storageConditions;
    private String description;
    private Medicine.MedicineStatus status;
    private Boolean isLowStock;
    private Boolean isExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static MedicineResponseDTO fromEntity(Medicine medicine) {
        return MedicineResponseDTO.builder()
                .id(medicine.getId())
                .name(medicine.getName())
                .genericName(medicine.getGenericName())
                .manufacturer(medicine.getManufacturer())
                .category(medicine.getCategory())
                .form(medicine.getForm())
                .strength(medicine.getStrength())
                .dosage(medicine.getDosage())
                .stockQuantity(medicine.getStockQuantity())
                .reorderLevel(medicine.getReorderLevel())
                .maximumStock(medicine.getMaximumStock())
                .unitPrice(medicine.getUnitPrice())
                .sellingPrice(medicine.getSellingPrice())
                .expiryDate(medicine.getExpiryDate())
                .batchNumber(medicine.getBatchNumber())
                .location(medicine.getLocation())
                .requiresPrescription(medicine.getRequiresPrescription())
                .prescriptionDetails(medicine.getPrescriptionDetails())
                .sideEffects(medicine.getSideEffects())
                .contraindications(medicine.getContraindications())
                .storageConditions(medicine.getStorageConditions())
                .description(medicine.getDescription())
                .status(medicine.getStatus())
                .isLowStock(medicine.isLowStock())
                .isExpired(medicine.isExpired())
                .createdAt(medicine.getCreatedAt())
                .updatedAt(medicine.getUpdatedAt())
                .build();
    }
}