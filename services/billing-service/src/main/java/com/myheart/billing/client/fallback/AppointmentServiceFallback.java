package com.myheart.billing.client.fallback;

import com.myheart.billing.client.AppointmentServiceClient;
import com.myheart.common.dto.AppointmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppointmentServiceFallback implements AppointmentServiceClient {
    
    @Override
    public AppointmentDTO getAppointmentById(String id) {
        log.error("Fallback: appointment-service indisponible pour getAppointmentById({})", id);
        return AppointmentDTO.builder()
            .id(id)
            .patientId("INCONNU")
            .doctorId("INCONNU")
            .status("UNKNOWN")
            .build();
    }
}