package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AdminAttendanceDto;
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
    private final LocationRepository   locationRepository;
    private final AttendanceMapper     attendanceMapper;
    private final GPSService           gpsService;
    private final QRService            qrService;

    // ── FilterableService wiring ──────────────────────────────────────────────

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

    // ── STUDENT: Mark attendance ──────────────────────────────────────────────

    /**
     * The only way a student can mark their attendance.
     *
     * Checks in order:
     *  1. Student exists with role = STUDENT
     *  2. Session exists
     *  3. Not already marked
     *  4. Session is currently active (now between startTime and endTime)
     *  5. QR token is valid:
     *       - Signature valid (not tampered)
     *       - Not expired
     *       - sessionId in token matches requested session
     *       - locationId in token matches session's actual DB location
     *       - Nonce not reused
     *  6. Student GPS is within 50 metres of the location from the QR token
     *
     * The GPS check uses the locationId extracted from the verified QR token
     * (not just the session's location field) — both must agree.
     */
    public AttendanceDto markAttendance(AttendanceDto dto) {

        // ── guard: mark-specific required fields ──────────────────────────────
        if (dto.getQrToken() == null || dto.getQrToken().isBlank()) {
            throw new CustomException("QR token is required", HttpStatus.BAD_REQUEST);
        }
        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            throw new CustomException(
                    "Latitude and longitude are required", HttpStatus.BAD_REQUEST);
        }

        // ── 1. resolve student ────────────────────────────────────────────────
        User student = resolveStudent(dto.getStudentId());

        // ── 2. resolve session ────────────────────────────────────────────────
        Session session = resolveSession(dto.getSessionId());

        // ── 3. not already marked ─────────────────────────────────────────────
        checkNotAlreadyMarked(dto.getStudentId(), dto.getSessionId());

        // ── 4. session time window ────────────────────────────────────────────
        LocalDateTime now = LocalDateTime.now();
        checkSessionWindow(session, now);

        // ── 5. verify QR token (checks signature, expiry, sessionId, locationId, nonce)
        QRService.VerificationResult qrResult =
                qrService.verifyQR(dto.getQrToken(), dto.getSessionId());

        if (!qrResult.isValid()) {
            throw new CustomException(
                    "❌ QR Code Invalid: " + qrResult.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        // ── 6. GPS check using the locationId from the verified QR token ──────
        //    This ensures the student is physically at the exact classroom
        //    that the QR was generated for — not just any 50m zone.
        Location qrLocation = locationRepository.findById(qrResult.getLocationId())
                .orElseThrow(() -> new CustomException(
                        "Location from QR not found: " + qrResult.getLocationId(),
                        HttpStatus.INTERNAL_SERVER_ERROR));

        GPSService.AttendanceCheckResult gpsResult = gpsService.checkDistance(
                dto.getLatitude(),       dto.getLongitude(),
                qrLocation.getLatitude(), qrLocation.getLongitude());

        if (!gpsResult.withinRange()) {
            throw new CustomException(
                    String.format("""
                            ❌ Attendance Denied — You are too far from the classroom!

                            🏫 Classroom     : %s
                            📍 Your distance : %.1f metres
                            ✅ Maximum allowed: %.0f metres
                            🧭 Walk towards  : %s
                            💡 Still need    : %.1f metres closer
                            """,
                            qrLocation.getName(),
                            gpsResult.distance(),
                            ALLOWED_RADIUS_METERS,
                            gpsResult.direction(),
                            gpsResult.distance() - ALLOWED_RADIUS_METERS),
                    HttpStatus.BAD_REQUEST);
        }

        // ── 7. determine PRESENT / LATE ───────────────────────────────────────
        Status status = now.isAfter(
                session.getStartTime().plusMinutes(LATE_THRESHOLD_MINUTES))
                ? Status.LATE
                : Status.PRESENT;

        // ── 8. save ───────────────────────────────────────────────────────────
        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(status);
        attendance.setTimestamp(now);
        attendance.setLatitude(dto.getLatitude());
        attendance.setLongitude(dto.getLongitude());

        AttendanceDto response = attendanceMapper.toDto(
                attendanceRepository.save(attendance));
        response.setMessage(status == Status.PRESENT
                ? "✅ Attendance marked as PRESENT"
                : "⚠️ Attendance marked as LATE — arrived more than "
                  + LATE_THRESHOLD_MINUTES + " minutes after session start");
        return response;
    }

    // ── ADMIN: Manual attendance ──────────────────────────────────────────────

    public AdminAttendanceDto createManualAttendance(AdminAttendanceDto dto) {
        User    student = resolveStudent(dto.getStudentId());
        Session session = resolveSession(dto.getSessionId());
        checkNotAlreadyMarked(dto.getStudentId(), dto.getSessionId());

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(dto.getStatus());
        attendance.setTimestamp(LocalDateTime.now());

        Attendance saved = attendanceRepository.save(attendance);

        AdminAttendanceDto response = new AdminAttendanceDto();
        response.setId(saved.getId());
        response.setStudentId(saved.getStudent().getId());
        response.setStudentName(saved.getStudent().getName());
        response.setSessionId(saved.getSession().getId());
        response.setSessionName(saved.getSession().getName());
        response.setStatus(saved.getStatus());
        response.setTimestamp(saved.getTimestamp());
        response.setMessage("✅ Attendance manually recorded as " + dto.getStatus());
        return response;
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
            throw new CustomException(
                    "Student not found with id: " + studentId, HttpStatus.NOT_FOUND);
        }
        return attendanceRepository.findByStudent_Id(studentId).stream()
                .map(attendanceMapper::toDto).collect(Collectors.toList());
    }

    public List<AttendanceDto> getBySessionId(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new CustomException(
                    "Session not found with id: " + sessionId, HttpStatus.NOT_FOUND);
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
            throw new CustomException(
                    "Attendance not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        attendanceRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
            throw new CustomException(
                    "❌ Session has not started yet. Starts at: " + session.getStartTime(),
                    HttpStatus.BAD_REQUEST);
        }
        if (now.isAfter(session.getEndTime())) {
            throw new CustomException(
                    "❌ Session has already ended. Ended at: " + session.getEndTime(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Attendance findOrThrow(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Attendance not found with id: " + id, HttpStatus.NOT_FOUND));
    }
}
