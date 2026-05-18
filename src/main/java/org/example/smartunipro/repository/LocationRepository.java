package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Location;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends FilterableRepository<Location, Long> {
}