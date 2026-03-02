package com.myheart.billing.repository;

import com.myheart.billing.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    List<Invoice> findByPatientIdOrderByCreatedAtDesc(String patientId);
    
    List<Invoice> findByPatientIdAndStatus(String patientId, Invoice.InvoiceStatus status);
    
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :now AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(@Param("now") LocalDateTime now);
    
    @Query("SELECT i FROM Invoice i WHERE i.issuedDate BETWEEN :start AND :end")
    List<Invoice> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(i.total) FROM Invoice i WHERE i.status = 'PAID' AND i.paidDate BETWEEN :start AND :end")
    BigDecimal getTotalRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT i.status, COUNT(i) FROM Invoice i GROUP BY i.status")
    List<Object[]> countByStatus();
    
    boolean existsByInvoiceNumber(String invoiceNumber);
}