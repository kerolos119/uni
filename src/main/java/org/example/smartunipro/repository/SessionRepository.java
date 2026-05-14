package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends FilterableRepository<Session, Long> {
    List<Session> findByCourseId(Long courseId);
    List<Session> findByInstructorId(Long instructorId);
}