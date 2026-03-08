package com.myheart.patient.client.fallback;

import com.myheart.common.dto.DoctorDTO;
import com.myheart.patient.client.DoctorServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DoctorServiceFallback implements DoctorServiceClient {
    
    @Override
    public DoctorDTO getDoctorById(String id) {
        log.error("Fallback: doctor-service indisponible pour getDoctorById({})", id);
        return DoctorDTO.builder()
            .id(id)
            .firstName("Médecin")
            .lastName("Non disponible")
            .specialty("Non spécifiée")
            .acceptingNewPatients(false)
            .build();
    }
    
    @Override
    public List<DoctorDTO> getDoctorsByPatient(String patientId) {
        log.error("Fallback: doctor-service indisponible pour getDoctorsByPatient({})", patientId);
        return Collections.emptyList();
    }
}