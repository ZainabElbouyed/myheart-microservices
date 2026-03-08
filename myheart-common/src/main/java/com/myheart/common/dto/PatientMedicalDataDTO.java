package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientMedicalDataDTO {
    private PatientDTO patient;
    private List<LabResultDTO> labResults;
    private List<PrescriptionDTO> prescriptions;
    private List<AppointmentDTO> appointments;
    private String summary;
}