package com.myheart.prescription.dto;

import com.myheart.prescription.entity.Prescription;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PrescriptionResponseDTO {
    
    private String id;
    private String prescriptionNumber;
    private String patientId;
    private String patientName;
    private LocalDate patientDateOfBirth;
    private String doctorId;
    private String doctorName;
    private String doctorLicenseNumber;
    private LocalDate prescriptionDate;
    private LocalDate expiryDate;
    private String diagnosis;
    private String clinicalNotes;
    private List<Prescription.Medication> medications;
    private Prescription.PrescriptionStatus status;
    private Integer refillsAllowed;
    private Integer refillsUsed;
    private Boolean isEmergency;
    private String pharmacyId;
    private String pharmacyName;
    private String insuranceId;
    private String priorAuthorizationNumber;
    private String specialInstructions;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime filledAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    public static PrescriptionResponseDTO fromEntity(Prescription prescription) {
        return PrescriptionResponseDTO.builder()
                .id(prescription.getId())
                .prescriptionNumber(prescription.getPrescriptionNumber())
                .patientId(prescription.getPatientId())
                .patientName(prescription.getPatientName())
                .patientDateOfBirth(prescription.getPatientDateOfBirth())
                .doctorId(prescription.getDoctorId())
                .doctorName(prescription.getDoctorName())
                .doctorLicenseNumber(prescription.getDoctorLicenseNumber())
                .prescriptionDate(prescription.getPrescriptionDate())
                .expiryDate(prescription.getExpiryDate())
                .diagnosis(prescription.getDiagnosis())
                .clinicalNotes(prescription.getClinicalNotes())
                .medications(prescription.getMedications())
                .status(prescription.getStatus())
                .refillsAllowed(prescription.getRefillsAllowed())
                .refillsUsed(prescription.getRefillsUsed())
                .isEmergency(prescription.getIsEmergency())
                .pharmacyId(prescription.getPharmacyId())
                .pharmacyName(prescription.getPharmacyName())
                .insuranceId(prescription.getInsuranceId())
                .priorAuthorizationNumber(prescription.getPriorAuthorizationNumber())
                .specialInstructions(prescription.getSpecialInstructions())
                .signedAt(prescription.getSignedAt())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .filledAt(prescription.getFilledAt())
                .cancelledAt(prescription.getCancelledAt())
                .cancellationReason(prescription.getCancellationReason())
                .build();
    }
}