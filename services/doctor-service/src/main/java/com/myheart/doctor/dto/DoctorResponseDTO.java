package com.myheart.doctor.dto;

import com.myheart.doctor.entity.Doctor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DoctorResponseDTO {
    
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String specialty;
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
    private List<String> availability;
    private Integer yearsOfExperience;
    private Double rating;
    private Integer numberOfReviews;
    private Boolean acceptingNewPatients;
    private String department;
    private String hospitalAffiliation;
    private String insuranceAccepted;
    private Doctor.DoctorStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static DoctorResponseDTO fromEntity(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .phoneNumber(doctor.getPhoneNumber())
                .specialty(doctor.getSpecialty())
                .licenseNumber(doctor.getLicenseNumber())
                .address(doctor.getAddress())
                .city(doctor.getCity())
                .postalCode(doctor.getPostalCode())
                .country(doctor.getCountry())
                .consultationFee(doctor.getConsultationFee())
                .biography(doctor.getBiography())
                .education(doctor.getEducation())
                .experience(doctor.getExperience())
                .languages(doctor.getLanguages())
                .profilePictureUrl(doctor.getProfilePictureUrl())
                .availability(doctor.getAvailability())
                .yearsOfExperience(doctor.getYearsOfExperience())
                .rating(doctor.getRating())
                .numberOfReviews(doctor.getNumberOfReviews())
                .acceptingNewPatients(doctor.getAcceptingNewPatients())
                .department(doctor.getDepartment())
                .hospitalAffiliation(doctor.getHospitalAffiliation())
                .insuranceAccepted(doctor.getInsuranceAccepted())
                .status(doctor.getStatus())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
}