package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.SessionType;

import java.time.LocalDateTime;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto extends Auditable {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Long id;

        @NotNull(message = "Location ID is required")
        private Long locationId;

        /** ID of a User with role = INSTRUCTOR */
        @NotNull(message = "Instructor ID is required")
        private Long instructorId;

        @NotNull(message = "Course ID is required")
        private Long courseId;

        @NotNull(message = "Session type is required")
        private SessionType type;

        @NotNull(message = "Start time is required")
        @Future(message = "Start time must be in the future")
        private LocalDateTime startTime;

        @NotNull(message = "End time is required")
        @Future(message = "End time must be in the future")
        private LocalDateTime endTime;

        @NotBlank(message = "Session name cannot be blank")
        @Size(min = 3, max = 100, message = "Session name must be between 3 and 100 characters")
        private String name;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String courseName;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String instructorName;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String locationName;
}