package org.example.smartunipro.mapper;

public interface Mapper<D,E> {
     D toDto (E entity);
     E toEntity (D dto);
     E updateToEntity(D dto , E entity);}
