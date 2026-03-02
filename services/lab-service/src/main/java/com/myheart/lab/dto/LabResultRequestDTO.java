package com.myheart.lab.dto;

import com.myheart.lab.entity.LabResult;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class LabResultRequestDTO {
    
    @NotBlank(message = "Patient ID is required")
    private String patientId;
    
    private String patientName;
    
    private String doctorId;
    
    private String doctorName;
    
    @NotBlank(message = "Test type is required")
    private String testType;
    
    private LocalDateTime testDate;
    
    private List<LabResult.TestParameter> parameters;
    
    private Map<String, Object> additionalResults;
    
    private String notes;
    
    private String technician;
    
    private String equipment;
    
    private String labName;
}