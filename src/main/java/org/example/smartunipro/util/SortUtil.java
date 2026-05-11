package org.example.smartunipro.util;

import org.springframework.data.domain.Sort;

import java.util.List;

public final class SortUtil {

    private SortUtil() {}

    public static Sort build(String sortBy,
                             String sortDir,
                             List<String> allowedFields) {
        String field = (sortBy != null && allowedFields.contains(sortBy))
                ? sortBy
                : allowedFields.get(0);

        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(dir, field);
    }
}