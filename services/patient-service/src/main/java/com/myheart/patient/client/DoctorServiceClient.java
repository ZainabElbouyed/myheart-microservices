package com.myheart.patient.client;

import com.myheart.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "doctor-service",
    path = "/api/doctors",
    contextId = "patientDoctorClient",
    fallback = com.myheart.patient.client.fallback.DoctorServiceFallback.class
)
public interface DoctorServiceClient {
    
    @GetMapping("/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") String id);
    
    @GetMapping("/by-patient/{patientId}")
    List<DoctorDTO> getDoctorsByPatient(@PathVariable("patientId") String patientId);
}