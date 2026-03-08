package com.myheart.appointment.client;

import com.myheart.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "patient-service",
    path = "/api/patients",
    contextId = "appointmentPatientClient",
    fallback = com.myheart.appointment.client.fallback.PatientServiceFallback.class
)
public interface PatientServiceClient {
    
    @GetMapping("/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);
    
    @GetMapping("/email/{email}")
    PatientDTO getPatientByEmail(@PathVariable("email") String email);
}