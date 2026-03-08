package com.myheart.prescription.client;

import com.myheart.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "patient-service",
    path = "/api/patients",
    contextId = "prescriptionPatientClient",
    fallback = com.myheart.prescription.client.fallback.PatientServiceFallback.class
)
public interface PatientServiceClient {
    
    @GetMapping("/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);
}