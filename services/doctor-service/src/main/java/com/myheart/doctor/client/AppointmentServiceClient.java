// services/doctor-service/src/main/java/com/myheart/doctor/client/AppointmentServiceClient.java
package com.myheart.doctor.client;

import com.myheart.common.dto.AppointmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "appointment-service", url = "${appointment.service.url:http://localhost:8084}")
public interface AppointmentServiceClient {
    
    @GetMapping("/api/appointments/doctor/{doctorId}")
    List<AppointmentDTO> getDoctorAppointments(@PathVariable("doctorId") String doctorId);
}