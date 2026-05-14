package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartunipro.model.EnrollmentStatus;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class EnrollmentFilterDto extends FilterDto {
    private Long             studentId;
    private Long             sessionId;
    private EnrollmentStatus status;
    private LocalDateTime    enrollmentDateFrom;
    private LocalDateTime    enrollmentDateTo;
}
