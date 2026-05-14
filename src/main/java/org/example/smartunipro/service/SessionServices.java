package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.SessionDto;
import org.example.smartunipro.dto.SessionFilterDto;
import org.example.smartunipro.entity.*;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.SessionMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServices extends FilterableService<Session, SessionDto, SessionFilterDto> {

    private static final List<String> SORTABLE_FIELDS =
            List.of("startTime", "endTime", "name", "type");

    private final SessionRepository  sessionRepository;
    private final CourseRepository   courseRepository;
    private final UserRepository     userRepository;
    private final LocationRepository locationRepository;
    private final SessionMapper      sessionMapper;

    // ── FilterableService ─────────────────────────────────────────────────────

    @Override protected List<String> sortableFields() { return SORTABLE_FIELDS; }
    @Override protected FilterableRepository<Session, ?> repository() { return sessionRepository; }
    @Override protected SessionDto toDto(Session e) { return sessionMapper.toDto(e); }

    @Override
    protected Specification<Session> toSpec(SessionFilterDto f) {
        return SpecificationBuilder.<Session>builder()
                .like("name",               f.getName())
                .equal("type",              f.getType())
                .equal("course.id",         f.getCourseId())
                .equal("instructor.id",     f.getInstructorId())
                .equal("location.id",       f.getLocationId())
                .greaterThanOrEqual("startTime", f.getStartTimeFrom())
                .lessThanOrEqual("startTime",    f.getStartTimeTo())
                .greaterThanOrEqual("endTime",   f.getEndTimeFrom())
                .lessThanOrEqual("endTime",      f.getEndTimeTo())
                .build();
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public SessionDto createSession(SessionDto dto) {
        return sessionMapper.toDto(
                sessionRepository.save(buildSessionFromDto(dto, new Session())));
    }

    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toDto).collect(Collectors.toList());
    }

    public SessionDto getById(Long id) {
        return sessionMapper.toDto(findOrThrow(id));
    }

    public SessionDto update(Long id, SessionDto dto) {
        Session session = findOrThrow(id);
        sessionMapper.updateToEntity(dto, session);
        if (dto.getCourseId()     != null) session.setCourse(resolveCourse(dto.getCourseId()));
        if (dto.getInstructorId() != null) session.setInstructor(resolveInstructor(dto.getInstructorId()));
        if (dto.getLocationId()   != null) session.setLocation(resolveLocation(dto.getLocationId()));
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    public void deleteById(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new CustomException("Session not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        sessionRepository.deleteById(id);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Session buildSessionFromDto(SessionDto dto, Session session) {
        session.setName(dto.getName());
        session.setType(dto.getType());
        session.setStartTime(dto.getStartTime());
        session.setEndTime(dto.getEndTime());
        session.setCourse(resolveCourse(dto.getCourseId()));
        session.setInstructor(resolveInstructor(dto.getInstructorId()));
        session.setLocation(resolveLocation(dto.getLocationId()));
        return session;
    }

    private Session findOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Course resolveCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private User resolveInstructor(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "User not found with id: " + id, HttpStatus.NOT_FOUND));
        if (user.getRole() != Role.INSTRUCTOR) {
            throw new CustomException(
                    "User with id " + id + " is not an INSTRUCTOR", HttpStatus.BAD_REQUEST);
        }
        return user;
    }

    private Location resolveLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Location not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}