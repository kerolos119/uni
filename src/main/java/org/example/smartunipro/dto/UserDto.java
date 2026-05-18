package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.smartunipro.model.Auditable;
import org.example.smartunipro.model.Role;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto extends Auditable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    /** Write-only: never returned in responses */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    // ── STUDENT fields (required when role = STUDENT) ─────────────────────────

    private String academicNumber;
    private String level;

    // ── INSTRUCTOR fields (required when role = INSTRUCTOR) ───────────────────

    private String department;
}