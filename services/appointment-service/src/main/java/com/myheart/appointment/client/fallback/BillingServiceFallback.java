package com.myheart.appointment.client.fallback;

import com.myheart.appointment.client.BillingServiceClient;
import com.myheart.common.dto.InvoiceDTO;
import com.myheart.common.dto.InvoiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BillingServiceFallback implements BillingServiceClient {
    
    @Override
    public InvoiceDTO createInvoice(InvoiceRequest request) {
        log.error("Fallback: billing-service indisponible pour createInvoice");
        return InvoiceDTO.builder()
            .id("FALLBACK-" + System.currentTimeMillis())
            .appointmentId(request.getAppointmentId())
            .patientId(request.getPatientId())
            .subtotal(request.getSubtotal())
            .total(0.0)
            .status("PENDING_RETRY")
            .warning("Facture en attente - service indisponible")
            .build();
    }
}