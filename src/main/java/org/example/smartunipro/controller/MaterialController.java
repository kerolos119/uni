package org.example.smartunipro.controller;


import org.example.smartunipro.dto.MaterialDto;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.entity.Material;
import org.example.smartunipro.service.MaterialServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialServices services;


    //  CREATE
    @PostMapping("/upload")
    public ResponseEntity<MaterialDto> materialUpload(
            @Valid @RequestBody MaterialDto request) {
        return new ResponseEntity<>(services.materialUpload(request), HttpStatus.CREATED);
    }

    //  GET ALL
    @GetMapping
    public ResponseEntity<Collection<Material>> getAll() {
        return ResponseEntity.ok(services.getAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Material> getById(@PathVariable Long id) {
        return ResponseEntity.ok(services.getById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<MaterialDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MaterialDto request) {
        return ResponseEntity.ok(services.update(id, request));
    }

    //  DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        services.delete(id);
        return ResponseEntity.noContent().build();
    }
}