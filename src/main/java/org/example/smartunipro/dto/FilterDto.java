package org.example.smartunipro.dto;

import lombok.Data;

@Data
public abstract class FilterDto {

    private String sortBy;
    private String sortDir="asc";
    
}
