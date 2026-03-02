package com.myheart.pharmacy.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_transactions")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InventoryTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false)
    private Integer quantity;
    
    private Integer previousStock;
    private Integer newStock;
    private String reference;
    private String patientId;
    private String doctorId;
    private String prescriptionId;
    private String notes;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    public enum TransactionType {
        PURCHASE, SALE, RETURN, ADJUSTMENT, EXPIRED, DAMAGED, TRANSFER
    }
}