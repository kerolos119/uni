package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseFilterDto extends FilterDto {
    private String name;
    private String code;
}