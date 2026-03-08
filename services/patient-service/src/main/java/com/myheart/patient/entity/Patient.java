package com.myheart.patient.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Patient {
    
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
    
    private LocalDate dateOfBirth;
    
    @Column(unique = true)
    private String socialSecurityNumber;
    
    private String bloodType;
    
    private String address;
    
    private String city;
    
    private String postalCode;
    
    private String country;
    
    private String emergencyContactName;
    
    private String emergencyContactPhone;
    
    private String emergencyContactRelation;
    
    private String insuranceProvider;
    
    private String insuranceNumber;
    
    private String medicalHistory;
    
    private String allergies;
    
    private String currentMedications;

    private String primaryDoctorId;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;
    
    private String occupation;
    
    private String profilePictureUrl;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }
    
    public enum MaritalStatus {
        SINGLE, MARRIED, DIVORCED, WIDOWED
    }
}