package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartunipro.model.Status;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceFilterDto extends FilterDto {
    private Long          studentId;
    private Long          sessionId;
    private Status        status;
    private LocalDateTime timestampFrom;
    private LocalDateTime timestampTo;
}
