package org.example.smartunipro.util;

import org.example.smartunipro.exception.CustomException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;

public final class SortUtil {

    private SortUtil() {}

    public static Sort build(String sortBy,
                             String sortDir,
                             List<String> allowedFields) {

        String field;
        if (sortBy == null || sortBy.isBlank()) {
            field = allowedFields.get(0);
        } else if (allowedFields.contains(sortBy)) {
            field = sortBy;
        } else {
            throw new CustomException(
                    "Invalid sort field: '" + sortBy + "'. Allowed fields: " + allowedFields,
                    HttpStatus.BAD_REQUEST);
        }

        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(dir, field);
    }
}