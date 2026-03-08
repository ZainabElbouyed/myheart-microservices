package com.myheart.lab.client.fallback;

import com.myheart.common.dto.PatientDTO;
import com.myheart.lab.client.PatientServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PatientServiceFallback implements PatientServiceClient {
    
    @Override
    public PatientDTO getPatientById(String id) {
        log.error("Fallback: patient-service indisponible pour getPatientById({})", id);
        return PatientDTO.builder()
            .id(id)
            .firstName("Patient")
            .lastName("Inconnu")
            .build();
    }
}