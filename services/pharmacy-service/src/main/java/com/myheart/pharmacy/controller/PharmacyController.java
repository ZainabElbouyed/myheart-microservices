package com.myheart.pharmacy.controller;

import com.myheart.pharmacy.dto.MedicineRequestDTO;
import com.myheart.pharmacy.dto.MedicineResponseDTO;
import com.myheart.pharmacy.dto.TransactionRequestDTO;
import com.myheart.pharmacy.entity.InventoryTransaction;
import com.myheart.pharmacy.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {
    
    private final PharmacyService pharmacyService;
    
    // ============ MÉDICAMENTS ============
    
    @PostMapping("/medicines")
    public ResponseEntity<MedicineResponseDTO> createMedicine(@Valid @RequestBody MedicineRequestDTO request) {
        MedicineResponseDTO response = pharmacyService.createMedicine(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/medicines")
    public ResponseEntity<List<MedicineResponseDTO>> getAllMedicines() {
        List<MedicineResponseDTO> medicines = pharmacyService.getAllMedicines();
        return ResponseEntity.ok(medicines);
    }
    
    @GetMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponseDTO> getMedicineById(@PathVariable String id) {
        MedicineResponseDTO response = pharmacyService.getMedicineById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/medicines/search")
    public ResponseEntity<List<MedicineResponseDTO>> searchMedicines(@RequestParam String q) {
        List<MedicineResponseDTO> medicines = pharmacyService.searchMedicines(q);
        return ResponseEntity.ok(medicines);
    }
    
    @GetMapping("/medicines/category/{category}")
    public ResponseEntity<List<MedicineResponseDTO>> getMedicinesByCategory(@PathVariable String category) {
        List<MedicineResponseDTO> medicines = pharmacyService.getMedicinesByCategory(category);
        return ResponseEntity.ok(medicines);
    }
    
    @GetMapping("/medicines/low-stock")
    public ResponseEntity<List<MedicineResponseDTO>> getLowStockMedicines() {
        List<MedicineResponseDTO> medicines = pharmacyService.getLowStockMedicines();
        return ResponseEntity.ok(medicines);
    }
    
    @GetMapping("/medicines/expiring")
    public ResponseEntity<List<MedicineResponseDTO>> getExpiringMedicines(@RequestParam(defaultValue = "30") int days) {
        List<MedicineResponseDTO> medicines = pharmacyService.getExpiringMedicines(days);
        return ResponseEntity.ok(medicines);
    }
    
    @PutMapping("/medicines/{id}")
    public ResponseEntity<MedicineResponseDTO> updateMedicine(
            @PathVariable String id,
            @Valid @RequestBody MedicineRequestDTO request) {
        MedicineResponseDTO response = pharmacyService.updateMedicine(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/medicines/{id}/stock")
    public ResponseEntity<MedicineResponseDTO> updateStock(
            @PathVariable String id,
            @RequestParam int quantity,
            @RequestParam(required = false) String reason) {
        MedicineResponseDTO response = pharmacyService.updateStock(id, quantity, reason);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/medicines/{id}/add-stock")
    public ResponseEntity<MedicineResponseDTO> addStock(
            @PathVariable String id,
            @RequestParam int quantity,
            @RequestParam(required = false) String reference) {
        MedicineResponseDTO response = pharmacyService.addStock(id, quantity, reference);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/medicines/{id}/dispense")
    public ResponseEntity<MedicineResponseDTO> dispenseMedicine(
            @PathVariable String id,
            @RequestParam int quantity,
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String prescriptionId) {
        MedicineResponseDTO response = pharmacyService.dispenseMedicine(id, quantity, patientId, doctorId, prescriptionId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/medicines/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String id) {
        pharmacyService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }
    
    // ============ TRANSACTIONS ============
    
    @GetMapping("/medicines/{medicineId}/transactions")
    public ResponseEntity<List<InventoryTransaction>> getMedicineTransactions(@PathVariable String medicineId) {
        List<InventoryTransaction> transactions = pharmacyService.getMedicineTransactions(medicineId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions/patient/{patientId}")
    public ResponseEntity<List<InventoryTransaction>> getPatientTransactions(@PathVariable String patientId) {
        List<InventoryTransaction> transactions = pharmacyService.getPatientTransactions(patientId);
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<InventoryTransaction> transactions = pharmacyService.getTransactionsByDateRange(start, end);
        return ResponseEntity.ok(transactions);
    }
    
    @PostMapping("/transactions")
    public ResponseEntity<InventoryTransaction> createTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        InventoryTransaction transaction = pharmacyService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    // ============ STATISTIQUES ============
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = pharmacyService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Pharmacy service is running");
    }
}