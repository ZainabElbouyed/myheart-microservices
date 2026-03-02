package com.myheart.pharmacy.exception;

public class MedicineNotFoundException extends RuntimeException {
    
    public MedicineNotFoundException(String message) {
        super(message);
    }
}