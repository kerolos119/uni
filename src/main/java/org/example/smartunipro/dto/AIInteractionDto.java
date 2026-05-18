package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.smartunipro.model.Auditable;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIInteractionDto extends Auditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Question is required")
    private String question;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String answer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime askedAt;

    /** ID of a User with role = STUDENT */
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;
}