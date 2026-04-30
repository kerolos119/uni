package org.example.smartunipro.controller;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.smartunipro.dto.EnrollmentDto;
import org.example.smartunipro.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@AllArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // CREATE
    @PostMapping
    public ResponseEntity<EnrollmentDto> create(@Valid @RequestBody EnrollmentDto request) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(request));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<EnrollmentDto>> getAll() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentDto> updateEnrollment(
            @PathVariable Long id,
            @Valid @RequestBody EnrollmentDto request) {

        EnrollmentDto updatedEnrollment = enrollmentService.update(id, request);
        return ResponseEntity.ok(updatedEnrollment);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.ok("Enrollment deleted successfully");
    }
}
