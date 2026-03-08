package com.myheart.prescription.client.fallback;

import com.myheart.common.dto.PharmacyDTO;
import com.myheart.common.dto.StockRequest;
import com.myheart.common.dto.DispenseRequest;
import com.myheart.prescription.client.PharmacyServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PharmacyServiceFallback implements PharmacyServiceClient {
    
    @Override
    public Boolean checkStock(StockRequest request) {
        log.error("Fallback: pharmacy-service indisponible pour checkStock - médicament: {}", 
                  request != null ? request.getMedicationId() : "unknown");
        return true; // Optimiste : on suppose que le stock est disponible
    }
    
    @Override
    public PharmacyDTO dispenseMedication(DispenseRequest request) {
        log.error("Fallback: pharmacy-service indisponible pour dispenseMedication - médicament: {}", 
                  request != null ? request.getMedicationId() : "unknown");
        
        return PharmacyDTO.builder()
            .id("FALLBACK")
            .name("Pharmacie indisponible")
            .status("PENDING_RETRY")
            .warning("Dispensation en attente - service pharmacie temporairement indisponible")
            .build();
    }
}