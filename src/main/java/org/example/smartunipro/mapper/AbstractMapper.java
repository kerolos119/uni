package org.example.smartunipro.mapper;

import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;



public abstract class AbstractMapper<D,E> implements Mapper<D, E> {
    @Autowired
    @Setter
    private ModelMapper modelMapper;

    private final Class<D> dClass;
    private final Class<E> eClass;

    public AbstractMapper(Class<D> dClass, Class<E> eClass) {
        this.dClass = dClass;
        this.eClass = eClass;
    }

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD)
                .setAmbiguityIgnored(true)
                .setPropertyCondition(Conditions.isNotNull());
    }

    @Override
    public D toDto(E entity) {
        return modelMapper.map(entity, dClass);
    }

    @Override
    public E toEntity(D dto) {
        return modelMapper.map(dto, eClass);
    }

    @Override
    public abstract E updateToEntity(D dto, E entity);
}
