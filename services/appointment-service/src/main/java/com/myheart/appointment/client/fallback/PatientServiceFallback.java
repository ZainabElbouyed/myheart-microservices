package com.myheart.appointment.client.fallback;

import com.myheart.appointment.client.PatientServiceClient;
import com.myheart.common.dto.PatientDTO;
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
            .lastName("Indisponible")
            .email("inconnu@fallback.com")
            .build();
    }
    
    @Override
    public PatientDTO getPatientByEmail(String email) {
        log.error("Fallback: patient-service indisponible pour getPatientByEmail({})", email);
        return PatientDTO.builder()
            .id("INCONNU")
            .firstName("Patient")
            .lastName("Indisponible")
            .email(email)
            .build();
    }
}