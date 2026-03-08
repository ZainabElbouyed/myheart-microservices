package com.myheart.doctor.client.fallback;

import com.myheart.common.dto.PrescriptionDTO;
import com.myheart.doctor.client.PrescriptionServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PrescriptionServiceFallback implements PrescriptionServiceClient {
    
    @Override
    public List<PrescriptionDTO> getPatientPrescriptions(String patientId) {
        log.error("Fallback: prescription-service indisponible pour getPatientPrescriptions({})", patientId);
        return Collections.emptyList();
    }
}