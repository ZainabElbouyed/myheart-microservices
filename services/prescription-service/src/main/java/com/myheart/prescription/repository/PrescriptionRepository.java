package com.myheart.prescription.repository;

import com.myheart.prescription.entity.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {
    
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(String patientId);
    
    List<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(String doctorId);
    
    List<Prescription> findByStatus(Prescription.PrescriptionStatus status);
    
    List<Prescription> findByPrescriptionNumber(String prescriptionNumber);
    
    @Query("{ 'patientId': ?0, 'status': 'ACTIVE' }")
    List<Prescription> findActivePrescriptionsByPatient(String patientId);
    
    @Query("{ 'patientId': ?0, 'expiryDate': { $gte: ?1 } }")
    List<Prescription> findValidPrescriptionsByPatient(String patientId, LocalDate currentDate);
    
    @Query("{ 'expiryDate': { $lt: ?0 }, 'status': 'ACTIVE' }")
    List<Prescription> findExpiredPrescriptions(LocalDate currentDate);
    
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    List<Prescription> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'patientId': ?0, 'medications.name': { $regex: ?1, $options: 'i' } }")
    List<Prescription> findByPatientAndMedication(String patientId, String medicationName);
    
    @Query(value = "{ 'patientId': ?0 }", count = true)
    long countByPatientId(String patientId);
    
    @Query("{ 'doctorId': ?0, 'prescriptionDate': { $gte: ?1, $lte: ?2 } }")
    List<Prescription> findByDoctorAndDateRange(String doctorId, LocalDate start, LocalDate end);
}