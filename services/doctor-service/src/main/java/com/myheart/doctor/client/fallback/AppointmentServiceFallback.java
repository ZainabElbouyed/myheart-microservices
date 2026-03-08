package com.myheart.doctor.client.fallback;

import com.myheart.common.dto.AppointmentDTO;
import com.myheart.doctor.client.AppointmentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class AppointmentServiceFallback implements AppointmentServiceClient {
    
    @Override
    public List<AppointmentDTO> getDoctorAppointments(String doctorId) {
        log.error("Fallback: appointment-service indisponible pour getDoctorAppointments({})", doctorId);
        return Collections.emptyList();
    }
    
    @Override
    public List<AppointmentDTO> getUpcomingAppointments(String doctorId) {
        log.error("Fallback: appointment-service indisponible pour getUpcomingAppointments({})", doctorId);
        return Collections.emptyList();
    }
}