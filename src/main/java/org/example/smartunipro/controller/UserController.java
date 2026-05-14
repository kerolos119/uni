package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.UserDto;
import org.example.smartunipro.dto.UserFilterDto;
import org.example.smartunipro.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    /**
     * POST /api/users
     * Body must include role. For STUDENT: academicNumber + level required.
     * For INSTRUCTOR: department required.
     */
    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(
            @PathVariable Long id,
            @RequestBody UserDto dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    /**
     * GET /api/users/filter?role=STUDENT&level=2&sortBy=name&sortDir=asc
     * GET /api/users/filter?role=INSTRUCTOR&department=CS
     * GET /api/users/filter?name=ali&sortBy=email
     */
    @GetMapping("/filter")
    public ResponseEntity<List<UserDto>> getFiltered(
            @ModelAttribute UserFilterDto filter) {
        return ResponseEntity.ok(userService.getFiltered(filter));
    }

    // ── Role shortcuts ────────────────────────────────────────────────────────

    /** GET /api/users/students — returns all users with role = STUDENT */
    @GetMapping("/students")
    public ResponseEntity<List<UserDto>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    /** GET /api/users/instructors — returns all users with role = INSTRUCTOR */
    @GetMapping("/instructors")
    public ResponseEntity<List<UserDto>> getAllInstructors() {
        return ResponseEntity.ok(userService.getAllInstructors());
    }
}