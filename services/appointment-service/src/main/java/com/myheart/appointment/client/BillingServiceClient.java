package com.myheart.appointment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "billing-service", url = "${billing.service.url:http://localhost:8085}")
public interface BillingServiceClient {
    
    @PostMapping("/api/billing/invoices")
    Map<String, Object> createInvoice(@RequestBody Map<String, Object> invoiceRequest);
}