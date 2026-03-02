package com.myheart.doctor.entity;

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
@Table(name = "doctors")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phoneNumber;
    
    @Column(nullable = false)
    private String specialty;
    
    @Column(unique = true, nullable = false)
    private String licenseNumber;
    
    private String address;
    
    private String city;
    
    private String postalCode;
    
    private String country;
    
    private BigDecimal consultationFee;
    
    private String biography;
    
    private String education;
    
    private String experience;
    
    private String languages;
    
    private String profilePictureUrl;
    
    @ElementCollection
    @CollectionTable(name = "doctor_availability", joinColumns = @JoinColumn(name = "doctor_id"))
    @Column(name = "availability")
    private List<String> availability = new ArrayList<>();
    
    private Integer yearsOfExperience;
    
    private Double rating;
    
    private Integer numberOfReviews;
    
    private Boolean acceptingNewPatients = true;
    
    private String department;
    
    private String hospitalAffiliation;
    
    private String insuranceAccepted;
    
    @Enumerated(EnumType.STRING)
    private DoctorStatus status = DoctorStatus.ACTIVE;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum DoctorStatus {
        ACTIVE, INACTIVE, ON_LEAVE, RETIRED
    }
    
    // Méthode utilitaire pour obtenir le nom complet
    public String getFullName() {
        return "Dr. " + firstName + " " + lastName;
    }
}