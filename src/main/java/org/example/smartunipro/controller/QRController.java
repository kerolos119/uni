package org.example.smartunipro.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.smartunipro.dto.QRDto;
import org.example.smartunipro.service.QRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    /**
     * POST /api/qr/generate/{sessionId}
     *
     * Generates a secure QR code for a session.
     * Only accessible by users with role = INSTRUCTOR (enforced by SecurityConfig).
     *
     * Reads session start time and classroom location directly from DB —
     * no manual parameters needed.
     *
     * Response:
     * {
     *   "sessionId":    2,
     *   "locationId":   1,
     *   "locationName": "Hall A",
     *   "expiresAt":    "2025-09-01T09:05:00",
     *   "rawToken":     "<JWT string — student scans this>",
     *   "qrImageBase64":"data:image/png;base64,..."
     * }
     *
     * The instructor's app displays qrImageBase64 as an image.
     * When the student scans the QR, the device reads rawToken and
     * sends it as the "qrToken" field in POST /api/attendance/mark.
     */
    @PostMapping("/generate/{sessionId}")
    public ResponseEntity<QRDto> generateQR(
            @PathVariable Long sessionId) {

        QRDto response = qrService.generateForSession(sessionId);
        log.info("📱 QR generated for session: {}, location: {}",
                response.getSessionId(), response.getLocationName());
        return ResponseEntity.ok(response);
    }
}
