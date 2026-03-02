package com.myheart.lab.dto;

import com.myheart.lab.entity.LabResult;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class LabResultResponseDTO {
    
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String testType;
    private LocalDateTime testDate;
    private LocalDateTime resultDate;
    private LabResult.LabStatus status;
    private List<LabResult.TestParameter> parameters;
    private Map<String, Object> additionalResults;
    private String summary;
    private String interpretation;
    private String recommendations;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String notes;
    private String technician;
    private String equipment;
    private String labName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static LabResultResponseDTO fromEntity(LabResult labResult) {
        return LabResultResponseDTO.builder()
                .id(labResult.getId())
                .patientId(labResult.getPatientId())
                .patientName(labResult.getPatientName())
                .doctorId(labResult.getDoctorId())
                .doctorName(labResult.getDoctorName())
                .testType(labResult.getTestType())
                .testDate(labResult.getTestDate())
                .resultDate(labResult.getResultDate())
                .status(labResult.getStatus())
                .parameters(labResult.getParameters())
                .additionalResults(labResult.getAdditionalResults())
                .summary(labResult.getSummary())
                .interpretation(labResult.getInterpretation())
                .recommendations(labResult.getRecommendations())
                .fileUrl(labResult.getFileUrl())
                .fileName(labResult.getFileName())
                .fileType(labResult.getFileType())
                .fileSize(labResult.getFileSize())
                .notes(labResult.getNotes())
                .technician(labResult.getTechnician())
                .equipment(labResult.getEquipment())
                .labName(labResult.getLabName())
                .createdAt(labResult.getCreatedAt())
                .updatedAt(labResult.getUpdatedAt())
                .build();
    }
}