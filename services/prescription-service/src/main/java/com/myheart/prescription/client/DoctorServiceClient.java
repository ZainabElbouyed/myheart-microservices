package com.myheart.prescription.client;

import com.myheart.common.dto.DoctorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "doctor-service",
    path = "/api/doctors",
    contextId = "prescriptionDoctorClient",
    fallback = com.myheart.prescription.client.fallback.DoctorServiceFallback.class
)
public interface DoctorServiceClient {
    
    @GetMapping("/{id}")
    DoctorDTO getDoctorById(@PathVariable("id") String id);
}