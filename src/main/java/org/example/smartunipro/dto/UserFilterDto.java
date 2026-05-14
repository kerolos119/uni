package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.smartunipro.model.Role;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFilterDto extends FilterDto {
    private String name;
    private String email;
    private Role   role;
}
