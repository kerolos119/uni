package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AdminAttendanceDto;
import org.example.smartunipro.dto.AttendanceDto;
import org.example.smartunipro.dto.AttendanceFilterDto;
import org.example.smartunipro.service.AttendanceServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceServices attendanceServices;

    // ── STUDENT endpoint ──────────────────────────────────────────────────────

    /**
     * POST /api/attendance/mark
     *
     * The ONLY way a student marks attendance.
     * Requires BOTH:
     *   - Valid QR token (correct session, not expired, not reused)
     *   - GPS location within 50 metres of the classroom
     *
     * Request body:
     * {
     *   "studentId": 1,
     *   "sessionId": 2,
     *   "qrToken":   "<token from /api/qr/generate>",
     *   "latitude":  30.0444,
     *   "longitude": 31.2357
     * }
     *
     * Response: status = PRESENT or LATE
     * Error 400: QR invalid OR student too far from class
     * Error 409: already marked
     */
    @PostMapping("/mark")
    public ResponseEntity<AttendanceDto> markAttendance(
            @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendanceServices.markAttendance(dto));
    }

    // ── ADMIN endpoints ───────────────────────────────────────────────────────

    /**
     * POST /api/attendance/admin/manual
     *
     * Admin manually records attendance for a student.
     * No QR or GPS required — admin sets the status directly.
     *
     * Request body:
     * {
     *   "studentId": 1,
     *   "sessionId": 2,
     *   "status":    "PRESENT" | "ABSENT" | "LATE"
     * }
     */
    @PostMapping("/admin/manual")
    public ResponseEntity<AdminAttendanceDto> createManual(
            @Valid @RequestBody AdminAttendanceDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(attendanceServices.createManualAttendance(dto));
    }

    // ── Read endpoints ────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<AttendanceDto>> getAll() {
        return ResponseEntity.ok(attendanceServices.getAllAttendance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceServices.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceDto>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceServices.getByStudentId(studentId));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<AttendanceDto>> getBySession(
            @PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceServices.getBySessionId(sessionId));
    }

    // ── Update / Delete ───────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDto> update(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.ok(attendanceServices.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    /**
     * GET /api/attendance/filter?studentId=1&sessionId=2&status=PRESENT
     *      &timestampFrom=2025-01-01T08:00:00&timestampTo=2025-12-31T23:59:59
     *      &sortBy=timestamp&sortDir=desc
     */
    @GetMapping("/filter")
    public ResponseEntity<List<AttendanceDto>> getFiltered(
            @ModelAttribute AttendanceFilterDto filter) {
        return ResponseEntity.ok(attendanceServices.getFiltered(filter));
    }
}