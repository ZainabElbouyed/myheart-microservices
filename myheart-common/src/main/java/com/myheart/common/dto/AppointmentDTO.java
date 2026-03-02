// myheart-common/src/main/java/com/myheart/common/dto/AppointmentDTO.java
package com.myheart.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private String id;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String type;
    private String reason;
}