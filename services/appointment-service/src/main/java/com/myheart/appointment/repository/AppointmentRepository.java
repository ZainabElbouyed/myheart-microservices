package com.myheart.appointment.repository;

import com.myheart.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    
    // Trouver par patient
    List<Appointment> findByPatientIdOrderByStartTimeDesc(String patientId);
    
    // Trouver par médecin
    List<Appointment> findByDoctorIdOrderByStartTimeDesc(String doctorId);
    
    // Trouver par statut
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    
    // Trouver par date
    List<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Trouver les rendez-vous d'un médecin à une date donnée
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND DATE(a.startTime) = :date")
    List<Appointment> findByDoctorIdAndDate(@Param("doctorId") String doctorId, @Param("date") LocalDate date);
    
    // Trouver les rendez-vous d'un patient à une date donnée
    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND DATE(a.startTime) = :date")
    List<Appointment> findByPatientIdAndDate(@Param("patientId") String patientId, @Param("date") LocalDate date);
    
    // Vérifier la disponibilité d'un médecin
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((a.startTime BETWEEN :start AND :end) OR (a.endTime BETWEEN :start AND :end))")
    boolean isDoctorAvailable(@Param("doctorId") String doctorId, 
                              @Param("start") LocalDateTime start, 
                              @Param("end") LocalDateTime end);
    
    // Compter les rendez-vous par statut
    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countByStatus();
    
    // Trouver les rendez-vous à venir pour un patient
    List<Appointment> findByPatientIdAndStartTimeAfterAndStatusNotIn(
            String patientId, 
            LocalDateTime now, 
            List<Appointment.AppointmentStatus> excludedStatuses);
    
    // Trouver les rendez-vous à venir pour un médecin
    List<Appointment> findByDoctorIdAndStartTimeAfterAndStatusNotIn(
            String doctorId, 
            LocalDateTime now, 
            List<Appointment.AppointmentStatus> excludedStatuses);
    
    // Trouver les rendez-vous du jour
    @Query("SELECT a FROM Appointment a WHERE DATE(a.startTime) = CURRENT_DATE")
    List<Appointment> findTodayAppointments();
    
    // Statistiques mensuelles
    @Query("SELECT COUNT(a), a.status FROM Appointment a " +
           "WHERE YEAR(a.startTime) = :year AND MONTH(a.startTime) = :month " +
           "GROUP BY a.status")
    List<Object[]> getMonthlyStats(@Param("year") int year, @Param("month") int month);
}