package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.MaterialDto;
import org.example.smartunipro.dto.MaterialFilterDto;
import org.example.smartunipro.service.MaterialServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialServices services;

    @PostMapping("/upload")
    public ResponseEntity<MaterialDto> materialUpload(
            @Valid @RequestBody MaterialDto request) {
        return new ResponseEntity<>(services.materialUpload(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MaterialDto>> getAllMaterial() {
        return ResponseEntity.ok(services.getAllMaterial());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDto> getMaterialById(@PathVariable Long id) {
        return ResponseEntity.ok(services.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDto> update(
            @PathVariable Long id,
            @Valid @RequestBody MaterialDto request) {
        return ResponseEntity.ok(services.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        services.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<MaterialDto>> getFiltered(
            @ModelAttribute MaterialFilterDto filter) {
        return ResponseEntity.ok(services.getFiltered(filter));
    }
}