package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Material;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends FilterableRepository<Material, Long> {
}