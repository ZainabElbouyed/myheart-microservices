package com.myheart.billing.repository;

import com.myheart.billing.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    
    List<Payment> findByInvoiceId(String invoiceId);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod method);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
}