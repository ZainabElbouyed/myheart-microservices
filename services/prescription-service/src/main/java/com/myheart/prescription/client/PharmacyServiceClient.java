package com.myheart.prescription.client;

import com.myheart.common.dto.PharmacyDTO;
import com.myheart.common.dto.StockRequest;
import com.myheart.common.dto.DispenseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "pharmacy-service",
    path = "/api/pharmacy",
    contextId = "prescriptionPharmacyClient",
    fallback = com.myheart.prescription.client.fallback.PharmacyServiceFallback.class
)
public interface PharmacyServiceClient {
    
    @PostMapping("/check-stock")
    Boolean checkStock(@RequestBody StockRequest request);
    
    @PostMapping("/dispense")
    PharmacyDTO dispenseMedication(@RequestBody DispenseRequest request);
}