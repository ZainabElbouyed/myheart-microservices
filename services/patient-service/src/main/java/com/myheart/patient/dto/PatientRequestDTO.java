package com.myheart.patient.dto;

import com.myheart.patient.entity.Patient;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDTO {
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Pattern(regexp = "^[0-9]{13,15}$", message = "Invalid social security number")
    private String socialSecurityNumber;
    
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood type")
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
}