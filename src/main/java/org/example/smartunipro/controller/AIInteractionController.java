package org.example.smartunipro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.smartunipro.dto.AIInteractionDto;
import org.example.smartunipro.service.AIInteractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIInteractionController {

    private final AIInteractionService aiInteractionService;

    @PostMapping("/ask")
    public ResponseEntity<AIInteractionDto> ask(
            @Valid @RequestBody AIInteractionDto requestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(aiInteractionService.ask(requestDto));
    }

    @GetMapping("/history/{studentId}")
    public ResponseEntity<List<AIInteractionDto>> getHistory(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(aiInteractionService.getHistory(studentId));
    }
}