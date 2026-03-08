package com.myheart.appointment.client;

import com.myheart.common.dto.InvoiceDTO;
import com.myheart.common.dto.InvoiceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "billing-service",
    path = "/api/billing",
    contextId = "appointmentBillingClient",
    fallback = com.myheart.appointment.client.fallback.BillingServiceFallback.class
)
public interface BillingServiceClient {
    
    @PostMapping("/invoices")
    InvoiceDTO createInvoice(@RequestBody InvoiceRequest request);
}