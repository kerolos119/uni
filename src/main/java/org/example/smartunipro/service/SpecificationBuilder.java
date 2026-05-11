package org.example.smartunipro.service;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collection;


public final class SpecificationBuilder<T> {

    private Specification<T> spec = Specification.where(null);

    private SpecificationBuilder() {}

    public static <T> SpecificationBuilder<T> builder() {
        return new SpecificationBuilder<>();
    }

    public SpecificationBuilder<T> like(String field, String value) {
        if (value == null || value.isBlank()) return this;
        spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(resolveField(root, field)),
                        "%" + value.toLowerCase() + "%"));
        return this;
    }

    public SpecificationBuilder<T> equal(String field, Object value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) ->
                cb.equal(resolveField(root, field), value));
        return this;
    }

    public <C extends Comparable<? super C>> SpecificationBuilder<T>
    greaterThanOrEqual(String field, C value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) ->
                cb.greaterThanOrEqualTo(resolveField(root, field), value));
        return this;
    }

    public <C extends Comparable<? super C>> SpecificationBuilder<T>
    lessThanOrEqual(String field, C value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) ->
                cb.lessThanOrEqualTo(resolveField(root, field), value));
        return this;
    }

    public <C extends Comparable<? super C>> SpecificationBuilder<T>
    greaterThan(String field, C value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) ->
                cb.greaterThan(resolveField(root, field), value));
        return this;
    }

    public <C extends Comparable<? super C>> SpecificationBuilder<T>
    lessThan(String field, C value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) ->
                cb.lessThan(resolveField(root, field), value));
        return this;
    }

    public <C extends Comparable<? super C>> SpecificationBuilder<T>
    between(String field, C from, C to) {
        return greaterThanOrEqual(field, from).lessThanOrEqual(field, to);
    }

    public SpecificationBuilder<T> isTrue(String field, Boolean value) {
        if (value == null) return this;
        spec = spec.and((root, q, cb) -> value
                ? cb.isTrue(resolveField(root, field))
                : cb.isFalse(resolveField(root, field)));
        return this;
    }

    public SpecificationBuilder<T> in(String field, Collection<?> values) {
        if (values == null || values.isEmpty()) return this;
        spec = spec.and((root, q, cb) ->
                resolveField(root, field).in(values));
        return this;
    }

    public SpecificationBuilder<T> isNull(String field) {
        spec = spec.and((root, q, cb) ->
                cb.isNull(resolveField(root, field)));
        return this;
    }

    public SpecificationBuilder<T> isNotNull(String field) {
        spec = spec.and((root, q, cb) ->
                cb.isNotNull(resolveField(root, field)));
        return this;
    }

    public SpecificationBuilder<T> custom(Specification<T> custom) {
        if (custom == null) return this;
        spec = spec.and(custom);
        return this;
    }

    public Specification<T> build() {
        return spec;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <V> jakarta.persistence.criteria.Path<V>
    resolveField(jakarta.persistence.criteria.Root<T> root, String field) {
        String[] parts = field.split("\\.");
        jakarta.persistence.criteria.Path path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }
}