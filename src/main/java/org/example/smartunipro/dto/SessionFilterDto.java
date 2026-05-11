package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartunipro.model.SessionType;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SessionFilterDto extends FilterDto {
    private String        name;
    private SessionType   type;
    private Long          courseId;
    private Long          instructorId;
    private Long          locationId;
    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;
    private LocalDateTime endTimeFrom;
    private LocalDateTime endTimeTo;
}