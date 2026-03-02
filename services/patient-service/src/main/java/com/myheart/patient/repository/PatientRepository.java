package com.myheart.patient.repository;

import com.myheart.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    Optional<Patient> findByEmail(String email);
    
    Optional<Patient> findBySocialSecurityNumber(String ssn);
    
    List<Patient> findByLastNameContainingIgnoreCase(String lastName);
    
    List<Patient> findByCityIgnoreCase(String city);
    
    List<Patient> findByBloodType(String bloodType);
    
    List<Patient> findByDateOfBirthBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "p.socialSecurityNumber LIKE CONCAT('%', :search, '%')")
    List<Patient> searchPatients(@Param("search") String searchTerm);
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.active = true")
    long countActivePatients();
    
    boolean existsByEmail(String email);
    
    boolean existsBySocialSecurityNumber(String ssn);
}