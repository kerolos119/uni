package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AttendanceDto;
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
}