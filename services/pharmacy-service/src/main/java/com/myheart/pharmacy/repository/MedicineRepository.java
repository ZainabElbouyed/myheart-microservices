package com.myheart.pharmacy.repository;

import com.myheart.pharmacy.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, String> {
    
    Optional<Medicine> findByNameAndStrength(String name, String strength);
    
    List<Medicine> findByCategory(String category);
    
    List<Medicine> findByManufacturer(String manufacturer);
    
    List<Medicine> findByRequiresPrescription(Boolean requiresPrescription);
    
    List<Medicine> findByStatus(Medicine.MedicineStatus status);
    
    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity <= m.reorderLevel")
    List<Medicine> findLowStockMedicines();
    
    @Query("SELECT m FROM Medicine m WHERE m.expiryDate <= :date")
    List<Medicine> findExpiringBefore(@Param("date") LocalDate date);
    
    @Query("SELECT m FROM Medicine m WHERE m.expiryDate BETWEEN :start AND :end")
    List<Medicine> findExpiringBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    @Query("SELECT m FROM Medicine m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.category) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Medicine> searchMedicines(@Param("search") String searchTerm);
    
    @Query("SELECT SUM(m.stockQuantity * m.unitPrice) FROM Medicine m")
    BigDecimal getTotalInventoryValue();
    
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = 'LOW_STOCK'")
    long countLowStockMedicines();
    
    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.status = 'EXPIRED'")
    long countExpiredMedicines();
}