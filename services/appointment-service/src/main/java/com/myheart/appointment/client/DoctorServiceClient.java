package com.myheart.appointment.client;

import com.myheart.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(
    name = "doctor-service",
    path = "/api/doctors",
    contextId = "appointmentDoctorClient",
    fallback = com.myheart.appointment.client.fallback.DoctorServiceFallback.class
)
public interface DoctorServiceClient {
    
    @GetMapping("/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") String id);
    
    @GetMapping("/by-specialty/{specialty}")
    List<DoctorDTO> getDoctorsBySpecialty(@PathVariable("specialty") String specialty);
    
    @GetMapping("/all")
    List<DoctorDTO> getAllDoctors();
    
    @GetMapping("/{id}/availability")
    Boolean checkAvailability(
        @PathVariable("id") String id,
        @RequestParam("start") LocalDateTime start,
        @RequestParam("end") LocalDateTime end
    );
}