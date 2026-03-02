package com.myheart.lab.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "lab_results")
@Data
@NoArgsConstructor
public class LabResult {
    
    @Id
    private String id;
    
    @Indexed
    private String patientId;
    
    private String patientName;
    
    @Indexed
    private String doctorId;
    
    private String doctorName;
    
    
    @Indexed
    private String testType;
    
    private LocalDateTime testDate;
    
    private LocalDateTime resultDate;
    
    @Indexed
    private LabStatus status = LabStatus.PENDING;
    
    private List<TestParameter> parameters;
    
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
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum LabStatus {
        PENDING,          // En attente d'analyse
        IN_PROGRESS,      // En cours d'analyse
        COMPLETED,        // Terminé
        REVIEWED,         // Révisé par le médecin
        CANCELLED,        // Annulé
        ABNORMAL          // Résultats anormaux
    }
    
    @Data
    @NoArgsConstructor
    public static class TestParameter {
        private String name;
        private String value;
        private String unit;
        private String referenceRange;
        private String interpretation;
        private Boolean isAbnormal;
    }
}