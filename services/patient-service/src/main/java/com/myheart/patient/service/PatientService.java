package com.myheart.patient.service;

import com.myheart.patient.dto.PatientRequestDTO;
import com.myheart.patient.dto.PatientResponseDTO;
import com.myheart.patient.entity.Patient;
import com.myheart.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    
    private final PatientRepository patientRepository;
    
    public PatientResponseDTO createPatient(PatientRequestDTO request) {
        // Vérifier si l'email existe déjà
        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Vérifier si le numéro de sécurité sociale existe déjà
        if (request.getSocialSecurityNumber() != null && 
            patientRepository.existsBySocialSecurityNumber(request.getSocialSecurityNumber())) {
            throw new RuntimeException("Social security number already exists");
        }
        
        Patient patient = mapToEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        return PatientResponseDTO.fromEntity(savedPatient);
    }
    
    public PatientResponseDTO getPatientById(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        return PatientResponseDTO.fromEntity(patient);
    }
    
    public PatientResponseDTO getPatientByEmail(String email) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found with email: " + email));
        return PatientResponseDTO.fromEntity(patient);
    }
    
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<PatientResponseDTO> searchPatients(String searchTerm) {
        return patientRepository.searchPatients(searchTerm)
                .stream()
                .map(PatientResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public PatientResponseDTO updatePatient(String id, PatientRequestDTO request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        
        updateEntity(patient, request);
        Patient updatedPatient = patientRepository.save(patient);
        return PatientResponseDTO.fromEntity(updatedPatient);
    }
    
    public void deletePatient(String id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
    
    public PatientResponseDTO deactivatePatient(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setActive(false);
        return PatientResponseDTO.fromEntity(patientRepository.save(patient));
    }
    
    public PatientResponseDTO activatePatient(String id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setActive(true);
        return PatientResponseDTO.fromEntity(patientRepository.save(patient));
    }
    
    public long getActivePatientCount() {
        return patientRepository.countActivePatients();
    }
    
    private Patient mapToEntity(PatientRequestDTO dto) {
        Patient patient = new Patient();
        updateEntity(patient, dto);
        return patient;
    }
    
    private void updateEntity(Patient patient, PatientRequestDTO dto) {
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setSocialSecurityNumber(dto.getSocialSecurityNumber());
        patient.setBloodType(dto.getBloodType());
        patient.setAddress(dto.getAddress());
        patient.setCity(dto.getCity());
        patient.setPostalCode(dto.getPostalCode());
        patient.setCountry(dto.getCountry());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setEmergencyContactRelation(dto.getEmergencyContactRelation());
        patient.setInsuranceProvider(dto.getInsuranceProvider());
        patient.setInsuranceNumber(dto.getInsuranceNumber());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setAllergies(dto.getAllergies());
        patient.setCurrentMedications(dto.getCurrentMedications());
        patient.setGender(dto.getGender());
        patient.setMaritalStatus(dto.getMaritalStatus());
        patient.setOccupation(dto.getOccupation());
    }
}