package org.example.smartunipro.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AttendanceDto;
import org.example.smartunipro.entity.*;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.mapper.AttendanceMapper;
import org.example.smartunipro.model.Status;
import org.example.smartunipro.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServices {

    private static final double ALLOWED_RADIUS_METERS = 50.0;
    private static final long   LATE_THRESHOLD_MINUTES = 15;

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository    studentRepository;
    private final SessionRepository    sessionRepository;
    private final AttendanceMapper     attendanceMapper;
    private final GPSService           gpsService;
    private final QRService qrService;

    public AttendanceDto markByLocation(AttendanceDto dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new CustomException(
                        "Student not found with id: " + dto.getStudentId(),
                        HttpStatus.NOT_FOUND));

        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + dto.getSessionId(),
                        HttpStatus.NOT_FOUND));

        if (attendanceRepository.existsByStudent_IdAndSession_Id(
                dto.getStudentId(), dto.getSessionId())) {
            throw new CustomException(
                    "Attendance already marked for this student and session",
                    HttpStatus.CONFLICT);
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(session.getStartTime())) {
            throw new CustomException(
                    "Session has not started yet", HttpStatus.BAD_REQUEST);
        }
        if (now.isAfter(session.getEndTime())) {
            throw new CustomException(
                    "Session has already ended", HttpStatus.BAD_REQUEST);
        }

        Location sessionLocation = session.getLocation();
        GPSService.AttendanceCheckResult result = gpsService.checkDistance(
                dto.getLatitude(), dto.getLongitude(),
                sessionLocation.getLatitude(), sessionLocation.getLongitude());

        if (!result.withinRange()) {
            throw new CustomException(
                    String.format("""
            ❌ Attendance Denied!
            
            📍 Your distance: %.1f meters
            ✅ Allowed distance: %.0f meters
            🧭 Direction to building: %s
            💡 Need to move: %.1f meters more
            """,
                            result.distance(),
                            ALLOWED_RADIUS_METERS,
                            result.direction(),
                            result.distance() - ALLOWED_RADIUS_METERS
                    ),
                    HttpStatus.BAD_REQUEST
            );
        }

        Status status = now.isAfter(session.getStartTime()
                .plusMinutes(LATE_THRESHOLD_MINUTES))
                ? Status.LATE
                : Status.PRESENT;

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(status);
        attendance.setTimestamp(now);
        attendance.setLatitude(dto.getLatitude());
        attendance.setLongitude(dto.getLongitude());

        Attendance saved = attendanceRepository.save(attendance);

        AttendanceDto response = attendanceMapper.toDto(saved);
        response.setMessage(status == Status.PRESENT
                ? "Attendance marked as PRESENT successfully"
                : "Attendance marked as LATE — you arrived after "
                  + LATE_THRESHOLD_MINUTES + " minutes");
        return response;
    }
    // أضف هذه الدالة الجديدة بعد markByLocation مباشرة
    public AttendanceDto markByLocationAndQR(AttendanceDto dto) {

        // ✅ 1. التحقق من QR Code
        QRService.VerificationResult qrResult = qrService.verifyQR(
                dto.getQrToken(),
                dto.getSessionId()
        );

        if (!qrResult.isValid()) {
            throw new CustomException(qrResult.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // ✅ 2. الباقي نفس markByLocation بالضبط
        return markByLocation(dto);
    }
    public AttendanceDto createAttendance(AttendanceDto dto) {

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new CustomException(
                        "Student not found with id: " + dto.getStudentId(),
                        HttpStatus.NOT_FOUND));

        Session session = sessionRepository.findById(dto.getSessionId())
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + dto.getSessionId(),
                        HttpStatus.NOT_FOUND));

        if (attendanceRepository.existsByStudent_IdAndSession_Id(
                dto.getStudentId(), dto.getSessionId())) {
            throw new CustomException(
                    "Attendance already exists for this student and session",
                    HttpStatus.CONFLICT);
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSession(session);
        attendance.setStatus(dto.getStatus());
        attendance.setTimestamp(LocalDateTime.now());

        return attendanceMapper.toDto(attendanceRepository.save(attendance));
    }

    public List<AttendanceDto> getAllAttendance() {
        return attendanceRepository.findAll()
                .stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    public AttendanceDto getById(Long id) {
        return attendanceMapper.toDto(findOrThrow(id));
    }

    public List<AttendanceDto> getByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new CustomException(
                    "Student not found with id: " + studentId, HttpStatus.NOT_FOUND);
        }
        return attendanceRepository.findByStudent_Id(studentId)
                .stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getBySessionId(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw new CustomException(
                    "Session not found with id: " + sessionId, HttpStatus.NOT_FOUND);
        }
        return attendanceRepository.findBySession_Id(sessionId)
                .stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
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


    private Attendance findOrThrow(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Attendance not found with id: " + id, HttpStatus.NOT_FOUND));
    }



}