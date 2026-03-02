package com.myheart.appointment.client;

import com.myheart.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${patient.service.url:http://localhost:8082}")
public interface PatientServiceClient {
    
    @GetMapping("/api/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);
    
    @GetMapping("/api/patients/email/{email}")
    PatientDTO getPatientByEmail(@PathVariable("email") String email);
}