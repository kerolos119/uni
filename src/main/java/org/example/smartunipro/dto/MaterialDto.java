package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.smartunipro.model.Auditable;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDto extends Auditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Material title cannot be blank")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Pattern(regexp = "^(http|https)://.*$", message = "PDF URL must be a valid URL")
    private String pdfUrl;

    @Pattern(regexp = "^(http|https)://.*$", message = "Video URL must be a valid URL")
    private String videoUrl;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    /** Populated by server on read — not required on write */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String courseName;
}