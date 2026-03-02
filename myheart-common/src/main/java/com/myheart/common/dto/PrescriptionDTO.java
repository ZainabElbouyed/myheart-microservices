// myheart-common/src/main/java/com/myheart/common/dto/PrescriptionDTO.java
package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private String id;
    private String prescriptionNumber;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDate prescriptionDate;
    private LocalDate expiryDate;
    private String diagnosis;
    private String status;
    private List<MedicationDTO> medications;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicationDTO {
        private String name;
        private String strength;
        private String dosage;
        private String frequency;
        private Integer quantity;
    }
}