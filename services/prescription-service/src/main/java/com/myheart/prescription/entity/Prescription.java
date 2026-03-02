package com.myheart.prescription.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "prescriptions")
@Data
@NoArgsConstructor
public class Prescription {
    
    @Id
    private String id;
    
    @Indexed
    private String prescriptionNumber;
    
    @Indexed
    private String patientId;
    
    private String patientName;
    
    private LocalDate patientDateOfBirth;
    
    @Indexed
    private String doctorId;
    
    private String doctorName;
    
    private String doctorLicenseNumber;
    
    @Indexed
    private LocalDate prescriptionDate;
    
    private LocalDate expiryDate;
    
    private String diagnosis;
    
    private String clinicalNotes;
    
    private List<Medication> medications;
    
    @Indexed
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;
    
    private Integer refillsAllowed = 0;
    
    private Integer refillsUsed = 0;
    
    private Boolean isEmergency = false;
    
    private String pharmacyId;
    
    private String pharmacyName;
    
    private String insuranceId;
    
    private String priorAuthorizationNumber;
    
    private String specialInstructions;
    
    private String signature;
    
    private LocalDateTime signedAt;
    
    @Indexed
    private String createdBy;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private LocalDateTime filledAt;
    
    private LocalDateTime cancelledAt;
    
    private String cancellationReason;
    
    public enum PrescriptionStatus {
        DRAFT,           // Brouillon
        ACTIVE,          // Active (en cours)
        FILLED,          // Délivrée
        PARTIALLY_FILLED, // Partiellement délivrée
        EXPIRED,         // Expirée
        CANCELLED,       // Annulée
        REFILLED         // Renouvelée
    }
    
    @Data
    @NoArgsConstructor
    public static class Medication {
        private String name;
        private String genericName;
        private String strength;
        private String form; // tablet, capsule, syrup, etc.
        private String dosage;
        private String frequency;
        private String duration;
        private String route; // oral, topical, intravenous, etc.
        private String instructions;
        private String indications;
        private Integer quantity;
        private Integer refills;
        private Boolean substituteAllowed = true;
        private String specialInstructions;
    }
}