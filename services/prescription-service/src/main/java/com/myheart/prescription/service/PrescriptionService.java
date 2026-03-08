package com.myheart.prescription.service;

import com.myheart.prescription.client.PatientServiceClient;
import com.myheart.prescription.client.DoctorServiceClient;
import com.myheart.prescription.client.PharmacyServiceClient;
import com.myheart.prescription.dto.PrescriptionRequestDTO;
import com.myheart.prescription.dto.PrescriptionResponseDTO;
import com.myheart.prescription.entity.Prescription;
import com.myheart.prescription.exception.PrescriptionNotFoundException;
import com.myheart.prescription.repository.PrescriptionRepository;
import com.myheart.common.dto.PatientDTO;
import com.myheart.common.dto.DoctorDTO;
import com.myheart.common.dto.PharmacyDTO;
import com.myheart.common.dto.StockRequest;
import com.myheart.common.dto.DispenseRequest;
import com.myheart.prescription.dto.MedicationDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;
    
    // ========== CLIENTS FEIGN AVEC CIRCUIT BREAKER ==========
    private final PatientServiceClient patientClient;
    private final DoctorServiceClient doctorClient;
    private final PharmacyServiceClient pharmacyClient;
    
    // ========== MÉTHODES AVEC CIRCUIT BREAKER ==========
    
    /**
     * Récupère les informations d'un patient
     */
    @CircuitBreaker(name = "patientService", fallbackMethod = "getPatientFallback")
    public PatientDTO getPatient(String patientId) {
        log.info("Appel à patient-service pour récupérer le patient: {}", patientId);
        return patientClient.getPatientById(patientId);
    }
    
    /**
     * Fallback pour getPatient
     */
    public PatientDTO getPatientFallback(String patientId, Exception e) {
        log.error("Fallback pour getPatient - patient-service indisponible: {}", e.getMessage());
        return PatientDTO.builder()
            .id(patientId)
            .firstName("Patient")
            .lastName("Inconnu")
            .email("inconnu@fallback.com")
            .build();
    }
    
    /**
     * Récupère les informations d'un médecin
     */
    @CircuitBreaker(name = "doctorService", fallbackMethod = "getDoctorFallback")
    public DoctorDTO getDoctor(String doctorId) {
        log.info("Appel à doctor-service pour récupérer le médecin: {}", doctorId);
        return doctorClient.getDoctorById(doctorId);
    }
    
    /**
     * Fallback pour getDoctor
     */
    public DoctorDTO getDoctorFallback(String doctorId, Exception e) {
        log.error("Fallback pour getDoctor - doctor-service indisponible: {}", e.getMessage());
        return DoctorDTO.builder()
            .id(doctorId)
            .firstName("Médecin")
            .lastName("Non disponible")
            .specialty("Non spécifiée")
            .build();
    }
    
    /**
     * Vérifie le stock et dispense un médicament
     */
    @CircuitBreaker(name = "pharmacyService", fallbackMethod = "dispenseFallback")
    public PharmacyDTO dispensePrescription(DispenseRequest request) {
        log.info("Vérification du stock pour le médicament: {}", request.getMedicationId());
        
        StockRequest stockRequest = new StockRequest();
        stockRequest.setMedicationId(request.getMedicationId());
        stockRequest.setQuantity(request.getQuantity());
        
        Boolean stockAvailable = pharmacyClient.checkStock(stockRequest);
        
        if (!stockAvailable) {
            throw new RuntimeException("Stock insuffisant pour le médicament: " + request.getMedicationId());
        }
        
        log.info("Stock disponible, dispensation du médicament");
        return pharmacyClient.dispenseMedication(request);
    }
    
    /**
     * Fallback pour dispensePrescription
     */
    public PharmacyDTO dispenseFallback(DispenseRequest request, Exception e) {
        log.error("Fallback pour dispensePrescription - pharmacy-service indisponible: {}", e.getMessage());
        return PharmacyDTO.builder()
            .id("FALLBACK-" + System.currentTimeMillis())
            .name("Pharmacie indisponible")
            .status("PENDING")
            .warning("Dispensation en attente - pharmacie temporairement indisponible")
            .build();
    }
    
    // ========== MÉTHODE DE CRÉATION MODIFIÉE ==========
    
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO request) {
        log.info("Creating new prescription for patient: {}", request.getPatientId());
        
        // Vérifier les informations du patient et du médecin si disponibles
        try {
            if (request.getPatientId() != null) {
                PatientDTO patient = getPatient(request.getPatientId());
                log.debug("Patient trouvé: {}", patient.getFirstName() + " " + patient.getLastName());
            }
            
            if (request.getDoctorId() != null) {
                DoctorDTO doctor = getDoctor(request.getDoctorId());
                log.debug("Médecin trouvé: {}", doctor.getFirstName() + " " + doctor.getLastName());
            }
        } catch (Exception e) {
            log.warn("Impossible de vérifier les informations patient/médecin, mais création continue: {}", e.getMessage());
        }
        
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber(generatePrescriptionNumber());
        prescription.setPatientId(request.getPatientId());
        prescription.setPatientName(request.getPatientName());
        prescription.setPatientDateOfBirth(request.getPatientDateOfBirth());
        prescription.setDoctorId(request.getDoctorId());
        prescription.setDoctorName(request.getDoctorName());
        prescription.setDoctorLicenseNumber(request.getDoctorLicenseNumber());
        prescription.setPrescriptionDate(request.getPrescriptionDate() != null ? 
                request.getPrescriptionDate() : LocalDate.now());
        prescription.setExpiryDate(calculateExpiryDate(request.getPrescriptionDate()));
        prescription.setDiagnosis(request.getDiagnosis());
        prescription.setClinicalNotes(request.getClinicalNotes());
        
        if (request.getMedications() != null) {
            List<Prescription.Medication> medications = request.getMedications().stream()
                    .map(this::convertToEntityMedication)
                    .collect(Collectors.toList());
            prescription.setMedications(medications);
        }
        
        prescription.setRefillsAllowed(request.getRefillsAllowed() != null ? 
                request.getRefillsAllowed() : 0);
        prescription.setRefillsUsed(0);
        prescription.setIsEmergency(request.getIsEmergency() != null ? 
                request.getIsEmergency() : false);
        prescription.setPharmacyId(request.getPharmacyId());
        prescription.setPharmacyName(request.getPharmacyName());
        prescription.setInsuranceId(request.getInsuranceId());
        prescription.setPriorAuthorizationNumber(request.getPriorAuthorizationNumber());
        prescription.setSpecialInstructions(request.getSpecialInstructions());
        prescription.setStatus(Prescription.PrescriptionStatus.ACTIVE);
        prescription.setCreatedBy(request.getDoctorId());
        
        // Signature électronique
        if (request.getSignature() != null) {
            prescription.setSignature(request.getSignature());
            prescription.setSignedAt(LocalDateTime.now());
        }
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription created successfully with number: {}", savedPrescription.getPrescriptionNumber());
        
        return PrescriptionResponseDTO.fromEntity(savedPrescription);
    }
    
    // ========== MÉTHODES EXISTANTES ==========
    
    public PrescriptionResponseDTO getPrescriptionById(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        return PrescriptionResponseDTO.fromEntity(prescription);
    }
    
    public PrescriptionResponseDTO getPrescriptionByNumber(String prescriptionNumber) {
        List<Prescription> prescriptions = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber);
        if (prescriptions.isEmpty()) {
            throw new PrescriptionNotFoundException("Prescription not found with number: " + prescriptionNumber);
        }
        return PrescriptionResponseDTO.fromEntity(prescriptions.get(0));
    }
    
    public List<PrescriptionResponseDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getPrescriptionsByPatient(String patientId) {
        return prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId)
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getActivePrescriptionsByPatient(String patientId) {
        return prescriptionRepository.findActivePrescriptionsByPatient(patientId)
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getValidPrescriptionsByPatient(String patientId) {
        return prescriptionRepository.findValidPrescriptionsByPatient(patientId, LocalDate.now())
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getPrescriptionsByDoctor(String doctorId) {
        return prescriptionRepository.findByDoctorIdOrderByPrescriptionDateDesc(doctorId)
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getPrescriptionsByStatus(Prescription.PrescriptionStatus status) {
        return prescriptionRepository.findByStatus(status)
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PrescriptionResponseDTO> getExpiredPrescriptions() {
        return prescriptionRepository.findExpiredPrescriptions(LocalDate.now())
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public PrescriptionResponseDTO updatePrescription(String id, PrescriptionRequestDTO request) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        
        // Mise à jour des champs modifiables
        if (request.getDiagnosis() != null) prescription.setDiagnosis(request.getDiagnosis());
        if (request.getClinicalNotes() != null) prescription.setClinicalNotes(request.getClinicalNotes());
        if (request.getMedications() != null) {
            List<Prescription.Medication> medications = request.getMedications().stream()
                    .map(this::convertToEntityMedication)
                    .collect(Collectors.toList());
            prescription.setMedications(medications);
        }
        if (request.getSpecialInstructions() != null) prescription.setSpecialInstructions(request.getSpecialInstructions());
        if (request.getPharmacyId() != null) prescription.setPharmacyId(request.getPharmacyId());
        if (request.getPharmacyName() != null) prescription.setPharmacyName(request.getPharmacyName());
        if (request.getInsuranceId() != null) prescription.setInsuranceId(request.getInsuranceId());
        if (request.getPriorAuthorizationNumber() != null) 
            prescription.setPriorAuthorizationNumber(request.getPriorAuthorizationNumber());
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription updated successfully with ID: {}", id);
        
        return PrescriptionResponseDTO.fromEntity(updatedPrescription);
    }
    
    private Prescription.Medication convertToEntityMedication(MedicationDTO dto) {
        Prescription.Medication medication = new Prescription.Medication();
        medication.setName(dto.getName());
        medication.setGenericName(dto.getGenericName());
        medication.setStrength(dto.getStrength());
        medication.setForm(dto.getForm());
        medication.setDosage(dto.getDosage());
        medication.setFrequency(dto.getFrequency());
        medication.setDuration(dto.getDuration());
        medication.setRoute(dto.getRoute());
        medication.setInstructions(dto.getInstructions());
        medication.setIndications(dto.getIndications());
        medication.setQuantity(dto.getQuantity());
        medication.setRefills(dto.getRefills());
        medication.setSubstituteAllowed(dto.getSubstituteAllowed() != null ? 
                dto.getSubstituteAllowed() : true);
        medication.setSpecialInstructions(dto.getSpecialInstructions());
        return medication;
    }
    
    public PrescriptionResponseDTO updatePrescriptionStatus(String id, Prescription.PrescriptionStatus status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        
        prescription.setStatus(status);
        
        switch (status) {
            case FILLED:
            case PARTIALLY_FILLED:
            case REFILLED:
                prescription.setFilledAt(LocalDateTime.now());
                break;
            case CANCELLED:
                prescription.setCancelledAt(LocalDateTime.now());
                break;
            case EXPIRED:
                // Logique pour marquer comme expiré
                break;
            case ACTIVE:
                // Rien à faire
                break;
            case DRAFT:
                // Rien à faire
                break;
            default:
                // Autres cas
                break;
        }
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription status updated to {} for ID: {}", status, id);
        
        return PrescriptionResponseDTO.fromEntity(updatedPrescription);
    }
    
    public PrescriptionResponseDTO fillPrescription(String id, String pharmacyId, String pharmacyName) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        
        if (prescription.getStatus() != Prescription.PrescriptionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot fill prescription with status: " + prescription.getStatus());
        }
        
        if (prescription.getExpiryDate() != null && prescription.getExpiryDate().isBefore(LocalDate.now())) {
            prescription.setStatus(Prescription.PrescriptionStatus.EXPIRED);
            prescriptionRepository.save(prescription);
            throw new IllegalStateException("Prescription has expired");
        }
        
        prescription.setPharmacyId(pharmacyId);
        prescription.setPharmacyName(pharmacyName);
        prescription.setStatus(Prescription.PrescriptionStatus.FILLED);
        prescription.setFilledAt(LocalDateTime.now());
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription filled successfully: {}", id);
        
        return PrescriptionResponseDTO.fromEntity(updatedPrescription);
    }
    
    public PrescriptionResponseDTO refillPrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        
        if (prescription.getRefillsUsed() >= prescription.getRefillsAllowed()) {
            throw new IllegalStateException("No refills remaining for this prescription");
        }
        
        if (prescription.getExpiryDate() != null && prescription.getExpiryDate().isBefore(LocalDate.now())) {
            prescription.setStatus(Prescription.PrescriptionStatus.EXPIRED);
            prescriptionRepository.save(prescription);
            throw new IllegalStateException("Prescription has expired");
        }
        
        prescription.setRefillsUsed(prescription.getRefillsUsed() + 1);
        prescription.setStatus(Prescription.PrescriptionStatus.REFILLED);
        prescription.setFilledAt(LocalDateTime.now());
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription refilled successfully: {}", id);
        
        return PrescriptionResponseDTO.fromEntity(updatedPrescription);
    }
    
    public PrescriptionResponseDTO cancelPrescription(String id, String reason) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new PrescriptionNotFoundException("Prescription not found with id: " + id));
        
        prescription.setStatus(Prescription.PrescriptionStatus.CANCELLED);
        prescription.setCancelledAt(LocalDateTime.now());
        prescription.setCancellationReason(reason);
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription cancelled: {}, reason: {}", id, reason);
        
        return PrescriptionResponseDTO.fromEntity(updatedPrescription);
    }
    
    public void deletePrescription(String id) {
        if (!prescriptionRepository.existsById(id)) {
            throw new PrescriptionNotFoundException("Prescription not found with id: " + id);
        }
        prescriptionRepository.deleteById(id);
        log.info("Prescription deleted: {}", id);
    }
    
    public List<PrescriptionResponseDTO> searchByPatientAndMedication(String patientId, String medicationName) {
        return prescriptionRepository.findByPatientAndMedication(patientId, medicationName)
                .stream()
                .map(PrescriptionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> getPrescriptionStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = prescriptionRepository.count();
        long active = prescriptionRepository.findByStatus(Prescription.PrescriptionStatus.ACTIVE).size();
        long expired = prescriptionRepository.findByStatus(Prescription.PrescriptionStatus.EXPIRED).size();
        long filled = prescriptionRepository.findByStatus(Prescription.PrescriptionStatus.FILLED).size();
        
        stats.put("total", total);
        stats.put("active", active);
        stats.put("expired", expired);
        stats.put("filled", filled);
        
        return stats;
    }
    
    private String generatePrescriptionNumber() {
        String prefix = "RX";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + datePart + "-" + uniquePart;
    }
    
    private LocalDate calculateExpiryDate(LocalDate prescriptionDate) {
        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
        }
        // Les prescriptions sont valables 1 an par défaut
        return prescriptionDate.plusYears(1);
    }
}