package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.SessionDto;
import org.example.smartunipro.entity.*;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.SessionMapper;
import org.example.smartunipro.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServices {

    private final SessionRepository    sessionRepository;
    private final CourseRepository     courseRepository;
    private final InstructorRepository instructorRepository;
    private final LocationRepository   locationRepository;
    private final SessionMapper        sessionMapper;

    public SessionDto createSession(SessionDto dto) {
        Session session = buildSessionFromDto(dto, new Session());
        return sessionMapper.toDto(sessionRepository.save(session));
    }

    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll()
                .stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public SessionDto getById(Long id) {
        return sessionMapper.toDto(findOrThrow(id));
    }

    public SessionDto update(Long id, SessionDto dto) {
        Session session = findOrThrow(id);
        sessionMapper.updateToEntity(dto, session);
        // re-resolve FK if client sent new IDs
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

    // ── helpers ─────────────────────────────────────────────────────────────

    private Session findOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + id, HttpStatus.NOT_FOUND));
    }

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

    private Course resolveCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Course not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Instructor resolveInstructor(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Instructor not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private Location resolveLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Location not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}