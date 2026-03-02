package com.myheart.lab.repository;

import com.myheart.lab.entity.LabResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LabResultRepository extends MongoRepository<LabResult, String> {
    
    List<LabResult> findByPatientIdOrderByTestDateDesc(String patientId);
    
    List<LabResult> findByDoctorIdOrderByTestDateDesc(String doctorId);
    
    List<LabResult> findByStatus(LabResult.LabStatus status);
    
    List<LabResult> findByDoctorIdAndStatus(String doctorId, String status);

    List<LabResult> findByTestTypeContainingIgnoreCase(String testType);
    
    
    @Query("{ 'patientId': ?0, 'status': 'COMPLETED' }")
    List<LabResult> findCompletedByPatient(String patientId);
    
    @Query("{ 'patientId': ?0, 'testDate': { $gte: ?1, $lte: ?2 } }")
    List<LabResult> findByPatientIdAndDateRange(String patientId, LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'doctorId': ?0, 'status': 'PENDING' }")
    List<LabResult> findPendingByDoctor(String doctorId);
    
    @Query(value = "{ 'patientId': ?0 }", count = true)
    long countByPatientId(String patientId);
    
    @Query("{ 'status': 'ABNORMAL' }")
    List<LabResult> findAbnormalResults();
    
    @Query("{ 'testDate': { $gte: ?0, $lte: ?1 } }")
    List<LabResult> findByDateRange(LocalDateTime start, LocalDateTime end);
}