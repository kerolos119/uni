package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.smartunipro.model.EnrollmentStatus;

import java.time.LocalDateTime;
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    private LocalDateTime EnrollmentDate;

    private String studentName;

    private String sessionName;

    private EnrollmentStatus status;

}
