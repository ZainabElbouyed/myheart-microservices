package com.myheart.pharmacy.repository;

import com.myheart.pharmacy.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, String> {
    
    List<InventoryTransaction> findByMedicineIdOrderByCreatedAtDesc(String medicineId);
    
    List<InventoryTransaction> findByType(InventoryTransaction.TransactionType type);
    
    List<InventoryTransaction> findByPatientId(String patientId);
    
    List<InventoryTransaction> findByPrescriptionId(String prescriptionId);
    
    @Query("SELECT t FROM InventoryTransaction t WHERE t.createdAt BETWEEN :start AND :end")
    List<InventoryTransaction> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(t.totalAmount) FROM InventoryTransaction t WHERE t.type = 'SALE' AND t.createdAt BETWEEN :start AND :end")
    Double getTotalSales(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}