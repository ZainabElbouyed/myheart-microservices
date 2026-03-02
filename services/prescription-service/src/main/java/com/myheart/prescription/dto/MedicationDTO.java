package com.myheart.prescription.dto;

import lombok.Data;

@Data
public class MedicationDTO {
    private String name;
    private String genericName;
    private String strength;
    private String form;
    private String dosage;
    private String frequency;
    private String duration;
    private String route;
    private String instructions;
    private String indications;
    private Integer quantity;
    private Integer refills;
    private Boolean substituteAllowed;
    private String specialInstructions;
}