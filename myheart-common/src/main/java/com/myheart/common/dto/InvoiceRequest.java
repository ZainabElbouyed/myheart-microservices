package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceRequest {
    private String patientId;
    private String patientName;
    private String appointmentId;
    private Double subtotal;
    private Double taxRate;
    private String description;
    private Map<String, Object> metadata;
}