package com.myheart.pharmacy.service;

import com.myheart.pharmacy.dto.MedicineRequestDTO;
import com.myheart.pharmacy.dto.MedicineResponseDTO;
import com.myheart.pharmacy.dto.TransactionRequestDTO;
import com.myheart.pharmacy.entity.InventoryTransaction;
import com.myheart.pharmacy.entity.Medicine;
import com.myheart.pharmacy.exception.InsufficientStockException;
import com.myheart.pharmacy.exception.MedicineNotFoundException;
import com.myheart.pharmacy.repository.InventoryTransactionRepository;
import com.myheart.pharmacy.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PharmacyService {
    
    private final MedicineRepository medicineRepository;
    private final InventoryTransactionRepository transactionRepository;
    
    // ============ GESTION DES MÉDICAMENTS ============
    
    public MedicineResponseDTO createMedicine(MedicineRequestDTO request) {
        log.info("Creating new medicine: {}", request.getName());
        
        Medicine medicine = new Medicine();
        mapToEntity(request, medicine);
        
        medicine.setStatus(Medicine.MedicineStatus.IN_STOCK);
        medicine.setStockQuantity(request.getInitialStock() != null ? request.getInitialStock() : 0);
        
        Medicine savedMedicine = medicineRepository.save(medicine);
        log.info("Medicine created successfully with ID: {}", savedMedicine.getId());
        
        // Créer une transaction initiale si stock > 0
        if (savedMedicine.getStockQuantity() > 0) {
            createInitialTransaction(savedMedicine);
        }
        
        return MedicineResponseDTO.fromEntity(savedMedicine);
    }
    
    public MedicineResponseDTO getMedicineById(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        return MedicineResponseDTO.fromEntity(medicine);
    }
    
    public List<MedicineResponseDTO> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(MedicineResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<MedicineResponseDTO> searchMedicines(String searchTerm) {
        return medicineRepository.searchMedicines(searchTerm)
                .stream()
                .map(MedicineResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<MedicineResponseDTO> getMedicinesByCategory(String category) {
        return medicineRepository.findByCategory(category)
                .stream()
                .map(MedicineResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<MedicineResponseDTO> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines()
                .stream()
                .map(MedicineResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<MedicineResponseDTO> getExpiringMedicines(int days) {
        LocalDate expiryDate = LocalDate.now().plusDays(days);
        return medicineRepository.findExpiringBefore(expiryDate)
                .stream()
                .map(MedicineResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public MedicineResponseDTO updateMedicine(String id, MedicineRequestDTO request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        
        mapToEntity(request, medicine);
        Medicine updatedMedicine = medicineRepository.save(medicine);
        log.info("Medicine updated successfully with ID: {}", id);
        
        return MedicineResponseDTO.fromEntity(updatedMedicine);
    }
    
    public MedicineResponseDTO updateStock(String id, int newQuantity, String reason) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        
        int oldQuantity = medicine.getStockQuantity();
        int difference = newQuantity - oldQuantity;
        
        if (difference != 0) {
            medicine.setStockQuantity(newQuantity);
            medicine.updateStatus();
            
            // Créer une transaction d'ajustement
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setMedicine(medicine);
            transaction.setType(InventoryTransaction.TransactionType.ADJUSTMENT);
            transaction.setQuantity(Math.abs(difference));
            transaction.setPreviousStock(oldQuantity);
            transaction.setNewStock(newQuantity);
            transaction.setNotes(reason);
            transaction.setCreatedBy("SYSTEM");
            
            if (difference > 0) {
                transaction.setNotes("Stock increase: " + reason);
            } else {
                transaction.setNotes("Stock decrease: " + reason);
            }
            
            transactionRepository.save(transaction);
        }
        
        Medicine updatedMedicine = medicineRepository.save(medicine);
        return MedicineResponseDTO.fromEntity(updatedMedicine);
    }
    
    public MedicineResponseDTO addStock(String id, int quantity, String reference) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        
        int previousStock = medicine.getStockQuantity();
        medicine.increaseStock(quantity);
        
        // Créer une transaction d'achat
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setMedicine(medicine);
        transaction.setType(InventoryTransaction.TransactionType.PURCHASE);
        transaction.setQuantity(quantity);
        transaction.setPreviousStock(previousStock);
        transaction.setNewStock(medicine.getStockQuantity());
        transaction.setReference(reference);
        transaction.setUnitPrice(medicine.getUnitPrice());
        transaction.setTotalAmount(medicine.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        transaction.setCreatedBy("SYSTEM");
        
        transactionRepository.save(transaction);
        
        Medicine updatedMedicine = medicineRepository.save(medicine);
        log.info("Added {} units to medicine: {}. New stock: {}", quantity, medicine.getName(), medicine.getStockQuantity());
        
        return MedicineResponseDTO.fromEntity(updatedMedicine);
    }
    
    @Transactional
    public MedicineResponseDTO dispenseMedicine(String id, int quantity, String patientId, String doctorId, String prescriptionId) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + id));
        
        if (medicine.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for " + medicine.getName() + 
                    ". Available: " + medicine.getStockQuantity() + ", Requested: " + quantity);
        }
        
        int previousStock = medicine.getStockQuantity();
        medicine.reduceStock(quantity);
        
        // Créer une transaction de vente
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setMedicine(medicine);
        transaction.setType(InventoryTransaction.TransactionType.SALE);
        transaction.setQuantity(quantity);
        transaction.setPreviousStock(previousStock);
        transaction.setNewStock(medicine.getStockQuantity());
        transaction.setPatientId(patientId);
        transaction.setDoctorId(doctorId);
        transaction.setPrescriptionId(prescriptionId);
        transaction.setUnitPrice(medicine.getSellingPrice() != null ? medicine.getSellingPrice() : medicine.getUnitPrice());
        transaction.setTotalAmount(transaction.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        transaction.setCreatedBy("PHARMACIST");
        
        transactionRepository.save(transaction);
        
        Medicine updatedMedicine = medicineRepository.save(medicine);
        log.info("Dispensed {} units of {} for patient: {}", quantity, medicine.getName(), patientId);
        
        return MedicineResponseDTO.fromEntity(updatedMedicine);
    }
    
    public void deleteMedicine(String id) {
        if (!medicineRepository.existsById(id)) {
            throw new MedicineNotFoundException("Medicine not found with id: " + id);
        }
        medicineRepository.deleteById(id);
        log.info("Medicine deleted with ID: {}", id);
    }
    
    // ============ GESTION DES TRANSACTIONS ============
    
    public List<InventoryTransaction> getMedicineTransactions(String medicineId) {
        return transactionRepository.findByMedicineIdOrderByCreatedAtDesc(medicineId);
    }
    
    public List<InventoryTransaction> getPatientTransactions(String patientId) {
        return transactionRepository.findByPatientId(patientId);
    }
    
    public List<InventoryTransaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByDateRange(start, end);
    }
    
    public InventoryTransaction createTransaction(TransactionRequestDTO request) {
        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new MedicineNotFoundException("Medicine not found with id: " + request.getMedicineId()));
        
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setMedicine(medicine);
        transaction.setType(request.getType());
        transaction.setQuantity(request.getQuantity());
        transaction.setPreviousStock(medicine.getStockQuantity());
        transaction.setReference(request.getReference());
        transaction.setPatientId(request.getPatientId());
        transaction.setDoctorId(request.getDoctorId());
        transaction.setPrescriptionId(request.getPrescriptionId());
        transaction.setNotes(request.getNotes());
        transaction.setCreatedBy(request.getCreatedBy());
        
        // Mettre à jour le stock selon le type de transaction
        switch (request.getType()) {
            case PURCHASE:
            case RETURN:
                medicine.increaseStock(request.getQuantity());
                break;
            case SALE:
            case EXPIRED:
            case DAMAGED:
                medicine.reduceStock(request.getQuantity());
                break;
            default:
                // ADJUSTMENT sera géré séparément
                break;
        }
        
        transaction.setNewStock(medicine.getStockQuantity());
        
        medicineRepository.save(medicine);
        InventoryTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", savedTransaction.getId());
        
        return savedTransaction;
    }
    
    // ============ STATISTIQUES ============
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMedicines = medicineRepository.count();
        long lowStock = medicineRepository.countLowStockMedicines();
        long expired = medicineRepository.countExpiredMedicines();
        BigDecimal totalValue = medicineRepository.getTotalInventoryValue();
        
        // Ventes du jour
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        Double todaySales = transactionRepository.getTotalSales(startOfDay, endOfDay);
        
        // Ventes du mois
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        Double monthSales = transactionRepository.getTotalSales(startOfMonth, endOfDay);
        
        stats.put("totalMedicines", totalMedicines);
        stats.put("lowStock", lowStock);
        stats.put("expired", expired);
        stats.put("totalInventoryValue", totalValue != null ? totalValue : BigDecimal.ZERO);
        stats.put("todaySales", todaySales != null ? todaySales : 0.0);
        stats.put("monthSales", monthSales != null ? monthSales : 0.0);
        
        return stats;
    }
    
    // ============ MÉTHODES PRIVÉES ============
    
    private void mapToEntity(MedicineRequestDTO dto, Medicine medicine) {
        medicine.setName(dto.getName());
        medicine.setGenericName(dto.getGenericName());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setCategory(dto.getCategory());
        medicine.setForm(dto.getForm());
        medicine.setStrength(dto.getStrength());
        medicine.setDosage(dto.getDosage());
        medicine.setReorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10);
        medicine.setMaximumStock(dto.getMaximumStock());
        medicine.setUnitPrice(dto.getUnitPrice());
        medicine.setSellingPrice(dto.getSellingPrice());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setBatchNumber(dto.getBatchNumber());
        medicine.setLocation(dto.getLocation());
        medicine.setRequiresPrescription(dto.getRequiresPrescription());
        medicine.setPrescriptionDetails(dto.getPrescriptionDetails());
        medicine.setSideEffects(dto.getSideEffects());
        medicine.setContraindications(dto.getContraindications());
        medicine.setStorageConditions(dto.getStorageConditions());
        medicine.setDescription(dto.getDescription());
    }
    
    private void createInitialTransaction(Medicine medicine) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setMedicine(medicine);
        transaction.setType(InventoryTransaction.TransactionType.PURCHASE);
        transaction.setQuantity(medicine.getStockQuantity());
        transaction.setPreviousStock(0);
        transaction.setNewStock(medicine.getStockQuantity());
        transaction.setNotes("Initial stock");
        transaction.setUnitPrice(medicine.getUnitPrice());
        transaction.setTotalAmount(medicine.getUnitPrice().multiply(BigDecimal.valueOf(medicine.getStockQuantity())));
        transaction.setCreatedBy("SYSTEM");
        
        transactionRepository.save(transaction);
    }
}