package org.example.smartunipro.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.SessionDto;
import org.example.smartunipro.service.SessionServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionServices services;



    @PostMapping("/create")
    public ResponseEntity<SessionDto> createSession(
            @Valid @RequestBody SessionDto request) {
        return new ResponseEntity<>(services.createSession(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        return ResponseEntity.ok(services.getAllSessions());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(services.getById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> update(
            @PathVariable Long id,
            @Valid @RequestBody SessionDto request) {
        return ResponseEntity.ok(services.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        services.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
