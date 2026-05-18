package org.example.smartunipro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Returned by POST /api/qr/generate/{sessionId}
 *
 * The instructor's app displays {@code qrImageBase64} as an image on screen.
 * The student's app scans it and reads {@code rawToken} from the QR,
 * then sends that token in the attendance request body.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRDto {

    /** Session this QR belongs to */
    private Long   sessionId;

    /** Location (classroom) this QR is locked to */
    private Long   locationId;

    /** Human-readable location name */
    private String locationName;

    /** When this QR expires (sessionStart + validityMinutes) */
    private LocalDateTime expiresAt;

    /**
     * The raw JWT string encoded inside the QR.
     * The student app reads this from the scanned QR image
     * and sends it as {@code qrToken} in the attendance request.
     */
    private String rawToken;

    /**
     * Base64-encoded PNG of the QR code.
     * Display this as an {@code <img>} in the instructor's UI.
     * Format: "data:image/png;base64,<data>"
     */
    private String qrImageBase64;
}
