package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Course;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends FilterableRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    boolean existsByCode(String code);
}