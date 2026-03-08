package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispenseRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String prescriptionId;
    private String medicationId;
    private String medicationName;
    private Integer quantity;
    private String pharmacyId;
    private String pharmacistId;
    private String patientId;
    private LocalDateTime dispenseDate;
    private String notes;
}