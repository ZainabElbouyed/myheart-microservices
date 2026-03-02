// services/doctor-service/src/main/java/com/myheart/doctor/client/LabServiceClient.java
package com.myheart.doctor.client;

import com.myheart.common.dto.LabResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "lab-service", url = "${lab.service.url:http://localhost:8086}")
public interface LabServiceClient {
    
    @GetMapping("/api/lab/patient/{patientId}")
    List<LabResultDTO> getPatientLabResults(@PathVariable("patientId") String patientId);
    
    @GetMapping("/api/lab/doctor/{doctorId}/pending")
    List<LabResultDTO> getPendingLabResults(@PathVariable("doctorId") String doctorId);
}