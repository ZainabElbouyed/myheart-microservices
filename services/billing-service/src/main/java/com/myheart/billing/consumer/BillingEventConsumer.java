// services/billing-service/src/main/java/com/myheart/billing/consumer/BillingEventConsumer.java
package com.myheart.billing.consumer;

import com.myheart.billing.entity.Invoice;
import com.myheart.billing.repository.InvoiceRepository;
import com.myheart.common.constants.RabbitMQConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BillingEventConsumer {
    
    private final InvoiceRepository invoiceRepository;
    
    @RabbitListener(queues = RabbitMQConstants.PAYMENT_QUEUE)
    public void handlePaymentProcessed(Map<String, Object> paymentEvent) {
        log.info("💰 Payment processed event received: {}", paymentEvent);
        
        String invoiceId = (String) paymentEvent.get("invoiceId");
        String status = (String) paymentEvent.get("status");
        String transactionId = (String) paymentEvent.get("transactionId");
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceId));
        
        invoice.setStatus(Invoice.InvoiceStatus.PAID);
        invoice.setPaidDate(LocalDateTime.now());
        invoiceRepository.save(invoice);
        
        log.info("✅ Invoice {} marked as PAID", invoiceId);
    }
    
    @RabbitListener(queues = RabbitMQConstants.INVOICE_QUEUE)
    public void handleInvoiceCreated(Map<String, Object> invoiceEvent) {
        log.info("📄 Invoice created event received: {}", invoiceEvent);
        // Logique supplémentaire si nécessaire (audit, analytics, etc.)
    }
}