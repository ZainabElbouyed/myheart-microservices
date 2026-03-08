package com.myheart.prescription.client.fallback;

import com.myheart.common.dto.DoctorDTO;
import com.myheart.prescription.client.DoctorServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
            .build();
    }
}