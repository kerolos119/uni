package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.Status;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminAttendanceDto extends Auditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Status is required")
    private Status status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime timestamp;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String studentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String sessionName;

    @Size(max = 500, message = "Message too long")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;
}
