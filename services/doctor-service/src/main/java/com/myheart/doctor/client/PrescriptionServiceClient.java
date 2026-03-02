// services/doctor-service/src/main/java/com/myheart/doctor/client/PrescriptionServiceClient.java
package com.myheart.doctor.client;

import com.myheart.common.dto.PrescriptionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "prescription-service", url = "${prescription.service.url:http://localhost:8087}")
public interface PrescriptionServiceClient {
    
    @GetMapping("/api/prescriptions/patient/{patientId}")
    List<PrescriptionDTO> getPatientPrescriptions(@PathVariable("patientId") String patientId);
}