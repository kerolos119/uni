package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.smartunipro.dto.EnrollmentDto;
import org.example.smartunipro.dto.EnrollmentFilterDto;
import org.example.smartunipro.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@AllArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDto> create(
            @Valid @RequestBody EnrollmentDto request) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(request));
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDto>> getAll() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDto> updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDto request) {
        return ResponseEntity.ok(enrollmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok("Enrollment deleted successfully");
    }

    /**
     * GET /api/enrollments/filter?studentId=1&sessionId=2&status=ACTIVE
     *      &enrollmentDateFrom=2025-01-01T00:00:00&sortBy=enrollmentDate&sortDir=desc
     */
    @GetMapping("/filter")
    public ResponseEntity<List<EnrollmentDto>> getFiltered(
            @ModelAttribute EnrollmentFilterDto filter) {
        return ResponseEntity.ok(enrollmentService.getFiltered(filter));
    }
}