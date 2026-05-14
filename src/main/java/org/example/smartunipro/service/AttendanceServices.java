package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AttendanceDto;
import org.example.smartunipro.dto.AttendanceFilterDto;
import org.example.smartunipro.entity.*;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.AttendanceMapper;
import org.example.smartunipro.model.Role;
import org.example.smartunipro.model.Status;
import org.example.smartunipro.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServices extends FilterableService<Attendance, AttendanceDto, AttendanceFilterDto> {

    private static final double ALLOWED_RADIUS_METERS  = 50.0;
    private static final long   LATE_THRESHOLD_MINUTES = 15;
    private static final List<String> SORTABLE_FIELDS  = List.of("timestamp", "status");

    private final AttendanceRepository attendanceRepository;
    private final UserRepository       userRepository;
    private final SessionRepository    sessionRepository;
    private final AttendanceMapper     attendanceMapper;
    private final GPSService           gpsService;
    private final QRService            qrService;

    // ── FilterableService ─────────────────────────────────────────────────────

    @Override protected List<String> sortableFields() { return SORTABLE_FIELDS; }
    @Override protected FilterableRepository<Attendance, ?> repository() { return attendanceRepository; }
    @Override protected AttendanceDto toDto(Attendance e) { return attendanceMapper.toDto(e); }

    @Override
    protected Specification<Attendance> toSpec(AttendanceFilterDto f) {
        return SpecificationBuilder.<Attendance>builder()
                .equal("student.id",  f.getStudentId())
                .equal("session.id",  f.getSessionId())
                .equal("status",      f.getStatus())
                .greaterThanOrEqual("timestamp", f.getTimestampFrom())
                .lessThanOrEqual("timestamp",    f.getTimestampTo())
                .build();
    }

    // ── Mark by GPS only ──────────────────────────────────────────────────────

    public AttendanceDto markByLocation(AttendanceDto dto) {
        User student  = resolveStudent(dto.getStudentId());
        Session session = resolveSession(dto.getSessionId());

        checkNotAlreadyMarked(dto.getStudentId(), dto.getSessionId());

        LocalDateTime now = LocalDateTime.now();
        checkSessionWindow(session, now);

        GPSService.AttendanceCheckResult result = gpsService.checkDistance(
                dto.getLatitude(), dto.getLongitude(),
                session.getLocation().getLatitude(),
                session.getLocation().getLongitude());

        if (!result.withinRange()) {
            throw new CustomException(
                    String.format("""
                            ❌ Attendance Denied!
                            
                            📍 Your distance: %.1f meters
                            ✅ Allowed distance: %.0f meters
                            🧭 Direction to building: %s
                            💡 Need to move: %.1f meters more
                            """,
                            result.distance(), ALLOWED_RADIUS_METERS,
                            result.direction(),
                            result.distance() - ALLOWED_RADIUS_METERS),
                    HttpStatus.BAD_REQUEST);
        }

        Status status = now.isAfter(
                session.getStartTime().plusMinutes(LATE_THRESHOLD_MINUTES))
                ? Status.LATE : Status.PRESENT;

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(status);
        attendance.setTimestamp(now);
        attendance.setLatitude(dto.getLatitude());
        attendance.setLongitude(dto.getLongitude());

        AttendanceDto response = attendanceMapper.toDto(attendanceRepository.save(attendance));
        response.setMessage(status == Status.PRESENT
                ? "Attendance marked as PRESENT successfully"
                : "Attendance marked as LATE — you arrived after " + LATE_THRESHOLD_MINUTES + " minutes");
        return response;
    }

    // ── Mark by QR + GPS ─────────────────────────────────────────────────────

    public AttendanceDto markByLocationAndQR(AttendanceDto dto) {
        QRService.VerificationResult qr = qrService.verifyQR(dto.getQrToken(), dto.getSessionId());
        if (!qr.isValid()) {
            throw new CustomException(qr.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return markByLocation(dto);
    }

    // ── Manual create (admin) ─────────────────────────────────────────────────

    public AttendanceDto createAttendance(AttendanceDto dto) {
        User student    = resolveStudent(dto.getStudentId());
        Session session = resolveSession(dto.getSessionId());
        checkNotAlreadyMarked(dto.getStudentId(), dto.getSessionId());

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(dto.getStatus());
        attendance.setTimestamp(LocalDateTime.now());

        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<AttendanceDto> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    public AttendanceDto getById(Long id) {
        return attendanceMapper.toDto(findOrThrow(id));
    }

    public List<AttendanceDto> getByStudentId(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new CustomException("Student not found with id: " + studentId, HttpStatus.NOT_FOUND);
        }
        return attendanceRepository.findByStudent_Id(studentId).stream()
                .map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    public List<AttendanceDto> getBySessionId(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new CustomException("Session not found with id: " + sessionId, HttpStatus.NOT_FOUND);
        }
        return attendanceRepository.findBySession_Id(sessionId).stream()
                .map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    public AttendanceDto update(Long id, AttendanceDto dto) {
        Attendance attendance = findOrThrow(id);
        attendanceMapper.updateToEntity(dto, attendance);
        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    public void deleteById(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new CustomException("Attendance not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        attendanceRepository.deleteById(id);
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

    private void checkNotAlreadyMarked(Long studentId, Long sessionId) {
        if (attendanceRepository.existsByStudent_IdAndSession_Id(studentId, sessionId)) {
            throw new CustomException(
                    "Attendance already marked for this student and session",
                    HttpStatus.CONFLICT);
        }
    }

    private void checkSessionWindow(Session session, LocalDateTime now) {
        if (now.isBefore(session.getStartTime())) {
            throw new CustomException("Session has not started yet", HttpStatus.BAD_REQUEST);
        }
        if (now.isAfter(session.getEndTime())) {
            throw new CustomException("Session has already ended", HttpStatus.BAD_REQUEST);
        }
    }

    private Attendance findOrThrow(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Attendance not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}