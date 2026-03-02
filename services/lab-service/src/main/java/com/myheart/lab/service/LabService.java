package com.myheart.lab.service;

import com.myheart.lab.dto.LabResultRequestDTO;
import com.myheart.lab.dto.LabResultResponseDTO;
import com.myheart.lab.entity.LabResult;
import com.myheart.lab.exception.LabResultNotFoundException;
import com.myheart.lab.repository.LabResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabService {
    
    private final LabResultRepository labResultRepository;
    private final String uploadDir = "uploads/lab/";
    
    public LabResultResponseDTO createLabResult(LabResultRequestDTO request) {
        log.info("Creating new lab result for patient: {}", request.getPatientId());
        
        LabResult labResult = new LabResult();
        labResult.setPatientId(request.getPatientId());
        labResult.setPatientName(request.getPatientName());
        labResult.setDoctorId(request.getDoctorId());
        labResult.setDoctorName(request.getDoctorName());
        labResult.setTestType(request.getTestType());
        labResult.setTestDate(request.getTestDate() != null ? request.getTestDate() : LocalDateTime.now());
        labResult.setParameters(request.getParameters());
        labResult.setAdditionalResults(request.getAdditionalResults());
        labResult.setNotes(request.getNotes());
        labResult.setTechnician(request.getTechnician());
        labResult.setEquipment(request.getEquipment());
        labResult.setLabName(request.getLabName());
        labResult.setStatus(LabResult.LabStatus.PENDING);
        
        
        LabResult savedResult = labResultRepository.save(labResult);
        log.info("Lab result created successfully with ID: {}", savedResult.getId());
        
        return LabResultResponseDTO.fromEntity(savedResult);
    }
    
    public LabResultResponseDTO getLabResultById(String id) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        return LabResultResponseDTO.fromEntity(labResult);
    }
    
    public List<LabResultResponseDTO> getAllLabResults() {
        return labResultRepository.findAll()
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<LabResultResponseDTO> getLabResultsByPatient(String patientId) {
        return labResultRepository.findByPatientIdOrderByTestDateDesc(patientId)
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<LabResultResponseDTO> getLabResultsByDoctor(String doctorId) {
        return labResultRepository.findByDoctorIdOrderByTestDateDesc(doctorId)
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<LabResultResponseDTO> getLabResultsByStatus(LabResult.LabStatus status) {
        return labResultRepository.findByStatus(status)
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // 👇 MÉTHODE CORRIGÉE : Renvoie des entités pour le contrôleur
    public List<LabResult> getPendingByDoctor(String doctorId) {
        log.info("Getting pending lab results for doctor: {}", doctorId);
        return labResultRepository.findByDoctorIdAndStatus(doctorId, "PENDING")
                .stream()
                .filter(result -> result.getStatus() == LabResult.LabStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    // 👇 MÉTHODE CORRIGÉE : Pour les résultats en attente (version DTO)
    public List<LabResultResponseDTO> getPendingResultsForDoctor(String doctorId) {
        return labResultRepository.findByDoctorIdAndStatus(doctorId, "PENDING")
                .stream()
                .filter(result -> result.getStatus() == LabResult.LabStatus.PENDING)
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // 👇 NOUVELLE MÉTHODE : Compléter un résultat labo
    public LabResult completeLabResult(String id, Map<String, Object> results) {
        log.info("Completing lab result: {}", id);
        
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        // Mettre à jour avec les résultats
        if (results.containsKey("parameters")) {
            // Traitement des paramètres si nécessaire
        }
        
        labResult.setStatus(LabResult.LabStatus.COMPLETED);
        labResult.setResultDate(LocalDateTime.now());
        
        return labResultRepository.save(labResult);
    }
    
    public List<LabResultResponseDTO> getAbnormalResults() {
        return labResultRepository.findAbnormalResults()
                .stream()
                .map(LabResultResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public LabResultResponseDTO updateLabResult(String id, LabResultRequestDTO request) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        if (request.getTestType() != null) labResult.setTestType(request.getTestType());
        if (request.getTestDate() != null) labResult.setTestDate(request.getTestDate());
        if (request.getParameters() != null) labResult.setParameters(request.getParameters());
        if (request.getAdditionalResults() != null) labResult.setAdditionalResults(request.getAdditionalResults());
        if (request.getNotes() != null) labResult.setNotes(request.getNotes());
        if (request.getTechnician() != null) labResult.setTechnician(request.getTechnician());
        if (request.getEquipment() != null) labResult.setEquipment(request.getEquipment());
        if (request.getLabName() != null) labResult.setLabName(request.getLabName());
        
        LabResult updatedResult = labResultRepository.save(labResult);
        return LabResultResponseDTO.fromEntity(updatedResult);
    }
    
    public LabResultResponseDTO updateLabStatus(String id, LabResult.LabStatus status) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        labResult.setStatus(status);
        
        if (status == LabResult.LabStatus.COMPLETED || status == LabResult.LabStatus.ABNORMAL) {
            labResult.setResultDate(LocalDateTime.now());
        }
        
        LabResult updatedResult = labResultRepository.save(labResult);
        log.info("Lab result {} status updated to: {}", id, status);
        
        return LabResultResponseDTO.fromEntity(updatedResult);
    }
    
    public LabResultResponseDTO addTestResults(String id, List<LabResult.TestParameter> parameters, String summary, String interpretation) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        labResult.setParameters(parameters);
        labResult.setSummary(summary);
        labResult.setInterpretation(interpretation);
        
        // Vérifier s'il y a des résultats anormaux
        boolean hasAbnormal = parameters.stream()
                .anyMatch(p -> p.getIsAbnormal() != null && p.getIsAbnormal());
        
        labResult.setStatus(hasAbnormal ? LabResult.LabStatus.ABNORMAL : LabResult.LabStatus.COMPLETED);
        labResult.setResultDate(LocalDateTime.now());
        
        LabResult updatedResult = labResultRepository.save(labResult);
        return LabResultResponseDTO.fromEntity(updatedResult);
    }
    
    public LabResultResponseDTO uploadFile(String id, MultipartFile file) throws IOException {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        // Créer le dossier d'upload si nécessaire
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Générer un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        
        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath);
        
        // Mettre à jour l'entité
        labResult.setFileUrl("/uploads/lab/" + fileName);
        labResult.setFileName(file.getOriginalFilename());
        labResult.setFileType(file.getContentType());
        labResult.setFileSize(file.getSize());
        
        LabResult updatedResult = labResultRepository.save(labResult);
        log.info("File uploaded for lab result: {}", id);
        
        return LabResultResponseDTO.fromEntity(updatedResult);
    }
    
    public LabResultResponseDTO reviewResult(String id, String interpretation, String recommendations) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new LabResultNotFoundException("Lab result not found with id: " + id));
        
        labResult.setInterpretation(interpretation);
        labResult.setRecommendations(recommendations);
        labResult.setStatus(LabResult.LabStatus.REVIEWED);
        
        LabResult updatedResult = labResultRepository.save(labResult);
        log.info("Lab result reviewed: {}", id);
        
        return LabResultResponseDTO.fromEntity(updatedResult);
    }
    
    public void deleteLabResult(String id) {
        if (!labResultRepository.existsById(id)) {
            throw new LabResultNotFoundException("Lab result not found with id: " + id);
        }
        labResultRepository.deleteById(id);
        log.info("Lab result deleted: {}", id);
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long total = labResultRepository.count();
        long pending = labResultRepository.findByStatus(LabResult.LabStatus.PENDING).size();
        long inProgress = labResultRepository.findByStatus(LabResult.LabStatus.IN_PROGRESS).size();
        long completed = labResultRepository.findByStatus(LabResult.LabStatus.COMPLETED).size();
        long abnormal = labResultRepository.findByStatus(LabResult.LabStatus.ABNORMAL).size();
        
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("inProgress", inProgress);
        stats.put("completed", completed);
        stats.put("abnormal", abnormal);
        
        return stats;
    }
}