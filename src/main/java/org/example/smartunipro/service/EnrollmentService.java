package org.example.smartunipro.service;


import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.EnrollmentDto;


import org.example.smartunipro.entity.Enrollment;

import org.example.smartunipro.entity.Session;
import org.example.smartunipro.entity.Student;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.EnrollmentMapper;
import org.example.smartunipro.repository.EnrollmentRepository;
import org.example.smartunipro.repository.SessionRepository;
import org.example.smartunipro.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;
    private final EnrollmentMapper enrollmentMapper;
    // POST
    public EnrollmentDto createEnrollment(EnrollmentDto dto) {
        Enrollment enrollment = buildEnrollmentFromDto(dto, new Enrollment());
        enrollment.setEnrollmentDate(LocalDateTime.now());

        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    // GET ALL
    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }
    // GET BY ID
    public EnrollmentDto getEnrollmentById(Long id) {
        return enrollmentMapper.toDto(findOrThrow(id));
    }

    //  UPDATE
    public EnrollmentDto update(Long id, EnrollmentDto dto) {
        Enrollment enrollment = findOrThrow(id);

        enrollmentMapper.updateToEntity(dto, enrollment);

        if (dto.getStudentId() != null) {
            enrollment.setStudent(resolveStudent(dto.getStudentId()));
        }

        if (dto.getSessionId() != null) {
            enrollment.setSession(resolveSession(dto.getSessionId()));
        }

        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }
    // DELETE
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new CustomException("Enrollment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
            enrollmentRepository.deleteById(id);
    }


// ── helpers ─────────────────────────────────────────────────────────────

    private Enrollment findOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Enrollment not found with id: " + id, HttpStatus.NOT_FOUND));
    }


    private Enrollment buildEnrollmentFromDto(EnrollmentDto dto, Enrollment enrollment) {
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());
        enrollment.setStatus(dto.getStatus());
        enrollment.setStudent(resolveStudent(dto.getStudentId()));
        enrollment.setSession(resolveSession(dto.getSessionId()));
        return enrollment;
    }


    private Student resolveStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Student not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Session resolveSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}
