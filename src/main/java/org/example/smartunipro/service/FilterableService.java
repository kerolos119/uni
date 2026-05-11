package org.example.smartunipro.service;

import org.example.smartunipro.dto.FilterDto;
import org.example.smartunipro.util.SortUtil;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public abstract class FilterableService<E, D, F extends FilterDto> {

    protected abstract List<String> sortableFields();

    protected abstract org.example.smartunipro.repository.FilterableRepository<E, ?> repository();

    protected abstract Specification<E> toSpec(F filter);

    protected abstract D toDto(E entity);

    public List<D> getFiltered(F filter) {
        Sort sort = SortUtil.build(
                filter.getSortBy(),
                filter.getSortDir(),
                sortableFields());

        return repository()
                .findAll(toSpec(filter), sort)
                .stream()
                .map(this::toDto)
                .toList();
    }
}