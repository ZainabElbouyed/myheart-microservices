package com.myheart.doctor.client.fallback;

import com.myheart.common.dto.LabResultDTO;
import com.myheart.doctor.client.LabServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class LabServiceFallback implements LabServiceClient {
    
    @Override
    public List<LabResultDTO> getPatientLabResults(String patientId) {
        log.error("Fallback: lab-service indisponible pour getPatientLabResults({})", patientId);
        return Collections.emptyList();
    }

    @Override
    public List<LabResultDTO> getPendingLabResults(String patientId) {
        log.error("Fallback: lab-service indisponible pour getPendingLabResults({})", patientId);
        return Collections.emptyList();
    }
}