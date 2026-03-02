// myheart-common/src/main/java/com/myheart/common/dto/LabResultDTO.java
package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultDTO {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String testType;
    private LocalDateTime testDate;
    private LocalDateTime resultDate;
    private String status;
    private List<Map<String, Object>> parameters;
    private String summary;
    private String interpretation;
}