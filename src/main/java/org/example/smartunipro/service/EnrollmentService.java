package org.example.smartunipro.service;


import lombok.AllArgsConstructor;
import org.example.smartunipro.dto.EnrollmentDto;
import org.example.smartunipro.entity.Enrollment;
import org.example.smartunipro.entity.Session;
import org.example.smartunipro.entity.Student;
import org.example.smartunipro.repository.EnrollmentRepository;
import org.example.smartunipro.repository.SessionRepository;
import org.example.smartunipro.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;

    // POST
    public EnrollmentDto createEnrollment(EnrollmentDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSession(session);
        enrollment.setContollmentDate(LocalDateTime.now());

        enrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(enrollment);
    }

    // GET ALL
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        return mapToResponse(enrollment);
    }

    // UPDATE
    public EnrollmentResponse updateEnrollment(Long id, EnrollmentDto request) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + request.getStudentId()));

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + request.getSessionId()));

        enrollment.setStudent(student);
        enrollment.setSession(session);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToResponse(updatedEnrollment);
    }

    // DELETE
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    // MAPPING
    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudentId(enrollment.getStudent().getId());
        response.setSessionId(enrollment.getSession().getId());
        response.setContollmentDate(enrollment.getContollmentDate());
        return response;
    }
}