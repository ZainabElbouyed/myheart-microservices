package com.myheart.patient.dto;

import com.myheart.patient.entity.Patient;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponseDTO {
    
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
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
    private Patient.Gender gender;
    private Patient.MaritalStatus maritalStatus;
    private String occupation;
    private String profilePictureUrl;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PatientResponseDTO fromEntity(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phoneNumber(patient.getPhoneNumber())
                .dateOfBirth(patient.getDateOfBirth())
                .socialSecurityNumber(patient.getSocialSecurityNumber())
                .bloodType(patient.getBloodType())
                .address(patient.getAddress())
                .city(patient.getCity())
                .postalCode(patient.getPostalCode())
                .country(patient.getCountry())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .emergencyContactRelation(patient.getEmergencyContactRelation())
                .insuranceProvider(patient.getInsuranceProvider())
                .insuranceNumber(patient.getInsuranceNumber())
                .medicalHistory(patient.getMedicalHistory())
                .allergies(patient.getAllergies())
                .currentMedications(patient.getCurrentMedications())
                .gender(patient.getGender())
                .maritalStatus(patient.getMaritalStatus())
                .occupation(patient.getOccupation())
                .profilePictureUrl(patient.getProfilePictureUrl())
                .active(patient.getActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}