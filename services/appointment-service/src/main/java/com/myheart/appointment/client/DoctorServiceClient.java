package com.myheart.appointment.client;

import com.myheart.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "doctor-service", url = "${doctor.service.url:http://localhost:8083}")
public interface DoctorServiceClient {
    
    @GetMapping("/api/doctors/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") String id);
    
    @GetMapping("/api/doctors")
    List<DoctorDTO> getAllDoctors();
    
    @GetMapping("/api/doctors/specialty/{specialty}")
    List<DoctorDTO> getDoctorsBySpecialty(@PathVariable("specialty") String specialty);
    
    @GetMapping("/api/doctors/{id}/check-availability")
    Boolean checkAvailability(
            @PathVariable("id") String id,
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end);
}