package com.myheart.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PrescriptionRequestDTO {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    private String patientName;
    private LocalDate patientDateOfBirth;
    
    @NotBlank(message = "Doctor ID is required")
    private String doctorId;
    
    private String doctorName;
    private String doctorLicenseNumber;
    
    @PastOrPresent(message = "Prescription date must be in the past or present")
    private LocalDate prescriptionDate;
    
    private String diagnosis;
    private String clinicalNotes;
    
    @NotNull(message = "At least one medication is required")
    private List<MedicationDTO> medications;
    
    private Integer refillsAllowed;
    private Boolean isEmergency;
    private String pharmacyId;
    private String pharmacyName;
    private String insuranceId;
    private String priorAuthorizationNumber;
    private String specialInstructions;
    private String signature;
}