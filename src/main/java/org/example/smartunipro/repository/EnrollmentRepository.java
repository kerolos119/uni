package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Enrollment;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends FilterableRepository<Enrollment, Long> {
}