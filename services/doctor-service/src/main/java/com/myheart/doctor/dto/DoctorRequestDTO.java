package com.myheart.doctor.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DoctorRequestDTO {
    
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
    
    @NotBlank(message = "Specialty is required")
    private String specialty;
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    private String address;
    private String city;
    private String postalCode;
    private String country;
    
    @Positive(message = "Consultation fee must be positive")
    private BigDecimal consultationFee;
    
    private String biography;
    private String education;
    private String experience;
    private String languages;
    private Integer yearsOfExperience;
    private String department;
    private String hospitalAffiliation;
    private String insuranceAccepted;
    private List<String> availability;
}