package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

            @NotBlank(message = "Material title cannot be blank")
            @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
            private String title;

            @Pattern(regexp = "^(http|https)://.*$", message = "PDF URL must be a valid URL")
            private String pdfUrl;

            @Pattern(regexp = "^(http|https)://.*$", message = "Video URL must be a valid URL")
            private String videoUrl;

            @NotNull(message = "Course ID is required") //لازم يحدد الكورس الى تابع ليه
            private Long courseId;

}
