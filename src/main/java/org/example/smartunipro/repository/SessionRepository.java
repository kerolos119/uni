package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByCourseId(Long courseId);
    List<Session> findByInstructorId(Long instructorId);
}