package com.myheart.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Medicine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    private String genericName;
    private String manufacturer;
    private String category;
    private String form;
    private String strength;
    private String dosage;
    
    @Column(nullable = false)
    private Integer stockQuantity = 0;
    
    private Integer reorderLevel = 10;
    private Integer maximumStock = 100;
    
    @Column(nullable = false)
    private BigDecimal unitPrice;

    
    private BigDecimal sellingPrice;
    private LocalDate expiryDate;
    private String batchNumber;
    private String location;
    private Boolean requiresPrescription = false;
    private String prescriptionDetails;
    private String sideEffects;
    private String contraindications;
    private String storageConditions;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private MedicineStatus status = MedicineStatus.IN_STOCK;
    
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryTransaction> transactions = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum MedicineStatus {
        IN_STOCK, LOW_STOCK, OUT_OF_STOCK, EXPIRED, DISCONTINUED
    }
    
    // Méthodes métier
    public boolean isLowStock() {
        return stockQuantity <= reorderLevel;
    }
    
    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }
    
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
    
    public void reduceStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
        updateStatus();
    }
    
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
        updateStatus();
    }
    
    public void updateStatus() {
        if (isExpired()) {
            this.status = MedicineStatus.EXPIRED;
        } else if (isOutOfStock()) {
            this.status = MedicineStatus.OUT_OF_STOCK;
        } else if (isLowStock()) {
            this.status = MedicineStatus.LOW_STOCK;
        } else {
            this.status = MedicineStatus.IN_STOCK;
        }
    }
    
}