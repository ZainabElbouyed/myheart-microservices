package com.myheart.pharmacy.client.fallback;

import com.myheart.common.dto.PrescriptionDTO;
import com.myheart.pharmacy.client.PrescriptionServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class PrescriptionServiceFallback implements PrescriptionServiceClient {
    
    @Override
    public PrescriptionDTO getPrescriptionById(String id) {
        log.error("Fallback: prescription-service indisponible pour getPrescriptionById({})", id);
        return PrescriptionDTO.builder()
            .id(id)
            .prescriptionNumber("UNKNOWN")
            .patientId("INCONNU")
            .doctorId("INCONNU")
            .status("UNKNOWN")
            .build();  // ✅ Retiré .warning()
    }
    
    @Override
    public List<PrescriptionDTO> getPatientPrescriptions(String patientId) {
        log.error("Fallback: prescription-service indisponible pour getPatientPrescriptions({})", patientId);
        return Collections.emptyList();
    }
    
    // ✅ AJOUT des méthodes manquantes
    @Override
    public PrescriptionDTO fillPrescription(String id, String pharmacyId, String pharmacistName) {
        log.error("Fallback: prescription-service indisponible pour fillPrescription({})", id);
        return PrescriptionDTO.builder()
            .id(id)
            .status("PENDING_FILL")
            .build();
    }
    
    @Override
    public List<PrescriptionDTO> getPrescriptionsByStatus(String status) {
        log.error("Fallback: prescription-service indisponible pour getPrescriptionsByStatus({})", status);
        return Collections.emptyList();
    }
}