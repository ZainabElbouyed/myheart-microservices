package com.myheart.doctor.service;

import com.myheart.doctor.client.*;
import com.myheart.doctor.dto.DoctorRequestDTO;
import com.myheart.doctor.dto.DoctorResponseDTO;
import com.myheart.doctor.entity.Doctor;
import com.myheart.doctor.exception.DoctorNotFoundException;
import com.myheart.doctor.repository.DoctorRepository;
import com.myheart.common.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    
    // ========== AJOUT DES CLIENTS FEIGN ==========
    private final PatientServiceClient patientClient;
    private final LabServiceClient labClient;
    private final PrescriptionServiceClient prescriptionClient;
    private final AppointmentServiceClient appointmentClient;
    
    // ========== MÉTHODES EXISTANTES (inchangées) ==========
    
    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        log.info("Creating new doctor with email: {}", request.getEmail());
        
        // Vérifier si l'email existe déjà
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Vérifier si le numéro de licence existe déjà
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists: " + request.getLicenseNumber());
        }
        
        Doctor doctor = mapToEntity(request);
        doctor.setStatus(Doctor.DoctorStatus.ACTIVE);
        doctor.setRating(0.0);
        doctor.setNumberOfReviews(0);
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor created successfully with ID: {}", savedDoctor.getId());
        
        return DoctorResponseDTO.fromEntity(savedDoctor);
    }
    
    public DoctorResponseDTO getDoctorById(String id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        return DoctorResponseDTO.fromEntity(doctor);
    }
    
    public DoctorResponseDTO getDoctorByEmail(String email) {
        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with email: " + email));
        return DoctorResponseDTO.fromEntity(doctor);
    }
    
    public DoctorResponseDTO getDoctorByLicenseNumber(String licenseNumber) {
        Doctor doctor = doctorRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with license: " + licenseNumber));
        return DoctorResponseDTO.fromEntity(doctor);
    }
    
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> searchDoctors(String searchTerm) {
        return doctorRepository.searchDoctors(searchTerm)
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialtyContainingIgnoreCase(specialty)
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department)
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getDoctorsByCity(String city) {
        return doctorRepository.findByCityIgnoreCase(city)
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getAvailableDoctors() {
        return doctorRepository.findByAcceptingNewPatientsTrue()
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getDoctorsByMaxFee(BigDecimal maxFee) {
        return doctorRepository.findByMaxConsultationFee(maxFee)
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DoctorResponseDTO> getTopRatedDoctors() {
        return doctorRepository.findTopRatedDoctors()
                .stream()
                .map(DoctorResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public DoctorResponseDTO updateDoctor(String id, DoctorRequestDTO request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        
        // Vérifier si l'email est modifié et déjà utilisé
        if (!doctor.getEmail().equals(request.getEmail()) && 
            doctorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Vérifier si le numéro de licence est modifié et déjà utilisé
        if (!doctor.getLicenseNumber().equals(request.getLicenseNumber()) && 
            doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new RuntimeException("License number already exists: " + request.getLicenseNumber());
        }
        
        updateEntity(doctor, request);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully with ID: {}", updatedDoctor.getId());
        
        return DoctorResponseDTO.fromEntity(updatedDoctor);
    }
    
    public DoctorResponseDTO updateDoctorStatus(String id, Doctor.DoctorStatus status) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        
        doctor.setStatus(status);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor status updated to {} for ID: {}", status, id);
        
        return DoctorResponseDTO.fromEntity(updatedDoctor);
    }
    
    public DoctorResponseDTO updateAcceptingPatients(String id, boolean accepting) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        
        doctor.setAcceptingNewPatients(accepting);
        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor accepting patients set to {} for ID: {}", accepting, id);
        
        return DoctorResponseDTO.fromEntity(updatedDoctor);
    }
    
    public DoctorResponseDTO updateRating(String id, Double newRating) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + id));
        
        // Calculer la nouvelle moyenne
        int currentReviews = doctor.getNumberOfReviews() != null ? doctor.getNumberOfReviews() : 0;
        double currentRating = doctor.getRating() != null ? doctor.getRating() : 0.0;
        
        double totalRating = (currentRating * currentReviews) + newRating;
        int newReviewCount = currentReviews + 1;
        double averageRating = totalRating / newReviewCount;
        
        doctor.setRating(Math.round(averageRating * 10) / 10.0); // Arrondir à 1 décimale
        doctor.setNumberOfReviews(newReviewCount);
        
        Doctor updatedDoctor = doctorRepository.save(doctor);
        return DoctorResponseDTO.fromEntity(updatedDoctor);
    }
    
    public void deleteDoctor(String id) {
        if (!doctorRepository.existsById(id)) {
            throw new DoctorNotFoundException("Doctor not found with id: " + id);
        }
        doctorRepository.deleteById(id);
        log.info("Doctor deleted with ID: {}", id);
    }
    
    public long getActiveDoctorCount() {
        return doctorRepository.countActiveDoctors();
    }
    
    // ========== NOUVELLES MÉTHODES AVEC CIRCUIT BREAKER ==========
    
    /**
     * Récupère un patient par son ID (appel à patient-service)
     */
    @CircuitBreaker(name = "patientService", fallbackMethod = "getPatientFallback")
    public PatientDTO getPatientById(String patientId) {
        log.info("Appel à patient-service pour récupérer le patient: {}", patientId);
        return patientClient.getPatientById(patientId);
    }
    
    /**
     * Fallback pour getPatientById
     */
    public PatientDTO getPatientFallback(String patientId, Exception e) {
        log.error("Fallback pour getPatientById - patient-service indisponible: {}", e.getMessage());
        return PatientDTO.builder()
            .id(patientId)
            .firstName("Patient")
            .lastName("Indisponible")
            .build();
    }
    
    /**
     * Récupère les résultats de laboratoire d'un patient (appel à lab-service)
     */
    @CircuitBreaker(name = "labService", fallbackMethod = "getLabResultsFallback")
    public List<LabResultDTO> getPatientLabResults(String patientId) {
        log.info("Appel à lab-service pour les résultats du patient: {}", patientId);
        return labClient.getPatientLabResults(patientId);
    }
    
    /**
     * Fallback pour getPatientLabResults
     */
    public List<LabResultDTO> getLabResultsFallback(String patientId, Exception e) {
        log.error("Fallback pour getPatientLabResults - lab-service indisponible: {}", e.getMessage());
        return Collections.emptyList();
    }
    
    /**
     * Récupère les prescriptions d'un patient (appel à prescription-service)
     */
    @CircuitBreaker(name = "prescriptionService", fallbackMethod = "getPrescriptionsFallback")
    public List<PrescriptionDTO> getPatientPrescriptions(String patientId) {
        log.info("Appel à prescription-service pour les prescriptions du patient: {}", patientId);
        return prescriptionClient.getPatientPrescriptions(patientId);
    }
    
    /**
     * Fallback pour getPatientPrescriptions
     */
    public List<PrescriptionDTO> getPrescriptionsFallback(String patientId, Exception e) {
        log.error("Fallback pour getPatientPrescriptions - prescription-service indisponible: {}", e.getMessage());
        return Collections.emptyList();
    }
    
    /**
     * Récupère les rendez-vous d'un docteur (appel à appointment-service)
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "getAppointmentsFallback")
    public List<AppointmentDTO> getDoctorAppointments(String doctorId) {
        log.info("Appel à appointment-service pour les rendez-vous du docteur: {}", doctorId);
        return appointmentClient.getDoctorAppointments(doctorId);
    }
    
    /**
     * Fallback pour getDoctorAppointments
     */
    public List<AppointmentDTO> getAppointmentsFallback(String doctorId, Exception e) {
        log.error("Fallback pour getDoctorAppointments - appointment-service indisponible: {}", e.getMessage());
        return Collections.emptyList();
    }
    
    /**
     * Récupère les rendez-vous à venir d'un docteur
     */
    @CircuitBreaker(name = "appointmentService", fallbackMethod = "getUpcomingAppointmentsFallback")
    public List<AppointmentDTO> getUpcomingAppointments(String doctorId) {
        log.info("Appel à appointment-service pour les rendez-vous à venir du docteur: {}", doctorId);
        return appointmentClient.getUpcomingAppointments(doctorId);
    }
    
    /**
     * Fallback pour getUpcomingAppointments
     */
    public List<AppointmentDTO> getUpcomingAppointmentsFallback(String doctorId, Exception e) {
        log.error("Fallback pour getUpcomingAppointments - appointment-service indisponible: {}", e.getMessage());
        return Collections.emptyList();
    }
    
    /**
     * Récupère les informations complètes d'un patient avec ses données médicales
     * Combine plusieurs appels avec Circuit Breakers
     */
    public PatientMedicalDataDTO getPatientMedicalData(String patientId) {
        log.info("Récupération des données médicales complètes pour le patient: {}", patientId);
        
        PatientDTO patient = getPatientById(patientId);
        List<LabResultDTO> labResults = getPatientLabResults(patientId);
        List<PrescriptionDTO> prescriptions = getPatientPrescriptions(patientId);
        
        return PatientMedicalDataDTO.builder()
            .patient(patient)
            .labResults(labResults)
            .prescriptions(prescriptions)
            .build();
    }
    
    /**
     * Vérifie si un docteur est disponible pour un créneau donné
     */
    public boolean isDoctorAvailable(String doctorId, LocalDateTime start, LocalDateTime end) {
        log.info("Vérification disponibilité docteur {} de {} à {}", doctorId, start, end);
        
        // Récupérer le docteur
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with id: " + doctorId));
        
        // Vérifier si le docteur accepte de nouveaux patients
        if (!doctor.getAcceptingNewPatients()) {
            return false;
        }
        
        // Vérifier les rendez-vous existants (à implémenter selon votre logique)
        // Cette partie dépend de comment vous stockez les disponibilités
        // Par exemple, si vous avez un repository de rendez-vous :
        // return !appointmentRepository.existsOverlappingAppointment(doctorId, start, end);
        
        // Pour l'instant, on suppose que le docteur est disponible
        return true;
    }
    // ========== MÉTHODES PRIVÉES EXISTANTES ==========
    
    private Doctor mapToEntity(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();
        updateEntity(doctor, dto);
        return doctor;
    }
    
    private void updateEntity(Doctor doctor, DoctorRequestDTO dto) {
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhoneNumber(dto.getPhoneNumber());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setAddress(dto.getAddress());
        doctor.setCity(dto.getCity());
        doctor.setPostalCode(dto.getPostalCode());
        doctor.setCountry(dto.getCountry());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setBiography(dto.getBiography());
        doctor.setEducation(dto.getEducation());
        doctor.setExperience(dto.getExperience());
        doctor.setLanguages(dto.getLanguages());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setDepartment(dto.getDepartment());
        doctor.setHospitalAffiliation(dto.getHospitalAffiliation());
        doctor.setInsuranceAccepted(dto.getInsuranceAccepted());
        doctor.setAvailability(dto.getAvailability());
    }
}