package org.example.smartunipro.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.LocationDto;
import org.example.smartunipro.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService service;


    @PostMapping("/create")
    public ResponseEntity<LocationDto> createLocation(
            @Valid @RequestBody LocationDto request) {
        return new ResponseEntity<>(service.createLocation(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(service.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LocationDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    }



