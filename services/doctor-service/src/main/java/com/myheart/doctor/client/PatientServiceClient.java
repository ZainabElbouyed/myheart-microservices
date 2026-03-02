// services/doctor-service/src/main/java/com/myheart/doctor/client/PatientServiceClient.java
package com.myheart.doctor.client;

import com.myheart.common.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "patient-service", url = "${patient.service.url:http://localhost:8082}")
public interface PatientServiceClient {
    
    @GetMapping("/api/patients/{id}")
    PatientDTO getPatientById(@PathVariable("id") String id);
    
    @GetMapping("/api/patients")
    List<PatientDTO> getAllPatients();
}