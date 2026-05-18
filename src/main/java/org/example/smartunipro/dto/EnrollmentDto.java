package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.EnrollmentStatus;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto extends Auditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /** ID of a User with role = STUDENT */
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    private LocalDateTime enrollmentDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String studentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sessionName;

    private EnrollmentStatus status;
}