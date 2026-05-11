package org.example.smartunipro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartunipro.service.QRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {   private final QRService qrService;

    @PostMapping("/generate/{sessionId}/{locationId}")
    public ResponseEntity<String> generateQR(
            @PathVariable Long sessionId,
            @PathVariable Long locationId,
            @RequestParam String sessionTime) {

        LocalDateTime time = LocalDateTime.parse(sessionTime);
        String qrCode = qrService.generateSecureQR(sessionId, locationId, time);

        log.info("📱 QR Code generated for session: {}, location: {}", sessionId, locationId);
        return ResponseEntity.ok(qrCode);
    }

}
