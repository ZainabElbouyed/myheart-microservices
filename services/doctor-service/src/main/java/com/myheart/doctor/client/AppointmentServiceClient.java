package com.myheart.doctor.client;

import com.myheart.common.dto.AppointmentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "appointment-service",
    path = "/api/appointments",
    contextId = "doctorAppointmentClient",
    fallback = com.myheart.doctor.client.fallback.AppointmentServiceFallback.class
)
public interface AppointmentServiceClient {
    
    @GetMapping("/doctor/{doctorId}")
    List<AppointmentDTO> getDoctorAppointments(@PathVariable("doctorId") String doctorId);
    
    @GetMapping("/doctor/{doctorId}/upcoming")
    List<AppointmentDTO> getUpcomingAppointments(@PathVariable("doctorId") String doctorId);
}