package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.InstructorDto;
import org.example.smartunipro.service.InstructorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    public ResponseEntity<InstructorDto> create(
            @Valid @RequestBody InstructorDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(instructorService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<InstructorDto>> getAll() {
        return ResponseEntity.ok(instructorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructorDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(instructorService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructorDto> update(
            @PathVariable Long id,
            @Valid @RequestBody InstructorDto dto) {
        return ResponseEntity.ok(instructorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        instructorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}