package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.EnrollmentDto;
import org.example.smartunipro.dto.EnrollmentFilterDto;
import org.example.smartunipro.entity.Enrollment;
import org.example.smartunipro.entity.Session;
import org.example.smartunipro.entity.User;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.EnrollmentMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.EnrollmentRepository;
import org.example.smartunipro.repository.FilterableRepository;
import org.example.smartunipro.repository.SessionRepository;
import org.example.smartunipro.repository.UserRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService extends FilterableService<Enrollment, EnrollmentDto, EnrollmentFilterDto> {

    private static final List<String> SORTABLE_FIELDS = List.of("enrollmentDate", "status");

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository       userRepository;
    private final SessionRepository    sessionRepository;
    private final EnrollmentMapper     enrollmentMapper;

    // ── FilterableService ─────────────────────────────────────────────────────

    @Override protected List<String> sortableFields() { return SORTABLE_FIELDS; }
    @Override protected FilterableRepository<Enrollment, ?> repository() { return enrollmentRepository; }
    @Override protected EnrollmentDto toDto(Enrollment e) { return enrollmentMapper.toDto(e); }

    @Override
    protected Specification<Enrollment> toSpec(EnrollmentFilterDto f) {
        return SpecificationBuilder.<Enrollment>builder()
                .equal("student.id",  f.getStudentId())
                .equal("session.id",  f.getSessionId())
                .equal("status",      f.getStatus())
                .greaterThanOrEqual("enrollmentDate", f.getEnrollmentDateFrom())
                .lessThanOrEqual("enrollmentDate",    f.getEnrollmentDateTo())
                .build();
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public EnrollmentDto createEnrollment(EnrollmentDto dto) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(resolveStudent(dto.getStudentId()));
        enrollment.setSession(resolveSession(dto.getSessionId()));
        enrollment.setStatus(dto.getStatus() != null ? dto.getStatus() : null);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    public List<EnrollmentDto> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollmentMapper::toDto).collect(Collectors.toList());
    }

    public EnrollmentDto getEnrollmentById(Long id) {
        return enrollmentMapper.toDto(findOrThrow(id));
    }

    public EnrollmentDto update(Long id, EnrollmentDto dto) {
        Enrollment enrollment = findOrThrow(id);
        enrollmentMapper.updateToEntity(dto, enrollment);
        if (dto.getStudentId() != null) enrollment.setStudent(resolveStudent(dto.getStudentId()));
        if (dto.getSessionId() != null) enrollment.setSession(resolveSession(dto.getSessionId()));
        return enrollmentMapper.toDto(enrollmentRepository.save(enrollment));
    }

    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new CustomException("Enrollment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        enrollmentRepository.deleteById(id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private User resolveStudent(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + id, HttpStatus.NOT_FOUND));
        if (user.getRole() != Role.STUDENT) {
            throw new CustomException(
                    "User with id " + id + " is not a STUDENT", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    private Session resolveSession(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Enrollment findOrThrow(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Enrollment not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}