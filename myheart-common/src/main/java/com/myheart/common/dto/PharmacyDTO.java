package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String phoneNumber;
    private String email;
    private String licenseNumber;
    private String pharmacistName;
    private String pharmacistLicense;
    private List<String> openingHours;
    private Boolean isOpen24Hours;
    private Double latitude;
    private Double longitude;
    private Boolean isActive;
    private String status;       // PENDING, ACTIVE, INACTIVE
    private String warning;      // Pour les messages de fallback
}