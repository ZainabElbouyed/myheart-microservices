package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String medicationId;
    private String medicationName;
    private Integer quantity;
    private String pharmacyId;
    private Boolean checkOnly;
}