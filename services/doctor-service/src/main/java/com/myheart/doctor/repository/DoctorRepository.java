package com.myheart.doctor.repository;

import com.myheart.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    
    Optional<Doctor> findByEmail(String email);
    
    Optional<Doctor> findByLicenseNumber(String licenseNumber);
    
    List<Doctor> findBySpecialtyContainingIgnoreCase(String specialty);
    
    List<Doctor> findByDepartment(String department);
    
    List<Doctor> findByCityIgnoreCase(String city);
    
    List<Doctor> findByAcceptingNewPatientsTrue();
    
    List<Doctor> findByStatus(Doctor.DoctorStatus status);
    
    @Query("SELECT d FROM Doctor d WHERE " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.specialty) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(d.department) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Doctor> searchDoctors(@Param("search") String searchTerm);
    
    @Query("SELECT d FROM Doctor d WHERE d.consultationFee <= :maxFee")
    List<Doctor> findByMaxConsultationFee(@Param("maxFee") BigDecimal maxFee);
    
    @Query("SELECT d FROM Doctor d ORDER BY d.rating DESC NULLS LAST")
    List<Doctor> findTopRatedDoctors();
    
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.status = 'ACTIVE'")
    long countActiveDoctors();
    
    boolean existsByEmail(String email);
    
    boolean existsByLicenseNumber(String licenseNumber);
}