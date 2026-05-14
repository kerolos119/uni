package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/mark-with-qr")
    public ResponseEntity<AttendanceDto> markAttendanceWithQR(
            @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceServices.markByLocationAndQR(dto));
    }

    @PostMapping("/mark")
    public ResponseEntity<AttendanceDto> markByLocation(
            @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceServices.markByLocation(dto));
    }

    @PostMapping
    public ResponseEntity<AttendanceDto> create(
            @Valid @RequestBody AttendanceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceServices.createAttendance(dto));
    }

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