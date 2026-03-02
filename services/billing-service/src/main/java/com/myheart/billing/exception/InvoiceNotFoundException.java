package com.myheart.billing.exception;

public class InvoiceNotFoundException extends RuntimeException {
    
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}