package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIInteractionDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Question is required")
    private String question;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String answer;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime askedAt;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String message;
}