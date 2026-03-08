package com.myheart.appointment.client.fallback;

import com.myheart.appointment.client.DoctorServiceClient;
import com.myheart.common.dto.DoctorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
            .lastName("Indisponible")
            .specialty("Non spécifiée")
            .acceptingNewPatients(false)
            .build();
    }
    
    @Override
    public List<DoctorDTO> getDoctorsBySpecialty(String specialty) {
        log.error("Fallback: doctor-service indisponible pour getDoctorsBySpecialty({})", specialty);
        return Collections.emptyList();
    }
    
    @Override
    public List<DoctorDTO> getAllDoctors() {
        log.error("Fallback: doctor-service indisponible pour getAllDoctors");
        return Collections.emptyList();
    }
    
    @Override
    public Boolean checkAvailability(String id, LocalDateTime start, LocalDateTime end) {
        log.error("Fallback: doctor-service indisponible pour checkAvailability");
        return true; // Mode dégradé : on accepte le rendez-vous
    }
}