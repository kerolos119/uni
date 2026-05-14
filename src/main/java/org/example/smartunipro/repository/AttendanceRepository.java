package org.example.smartunipro.repository;

import org.example.smartunipro.entity.Attendance;
import org.example.smartunipro.model.Status;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends FilterableRepository<Attendance, Long> {
    Optional<Attendance> findByStudent_IdAndSession_Id(Long studentId, Long sessionId);
    List<Attendance> findByStudent_Id(Long studentId);
    List<Attendance> findBySession_Id(Long sessionId);
    List<Attendance> findBySession_IdAndStatus(Long sessionId, Status status);
    boolean existsByStudent_IdAndSession_Id(Long studentId, Long sessionId);
}