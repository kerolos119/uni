package org.example.smartunipro.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MaterialFilterDto extends FilterDto {
    private String title;
    private Long   courseId;
    private String courseName;
    /** true = has PDF, false = no PDF, null = any */
    private Boolean hasPdf;
    /** true = has Video, false = no Video, null = any */
    private Boolean hasVideo;
}
