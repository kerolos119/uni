package org.example.smartunipro.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.example.smartunipro.dto.QRDto;
import org.example.smartunipro.entity.Session;
import org.example.smartunipro.exception.CustomException;
import org.example.smartunipro.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class QRService {

    @Value("${qr.secret}")
    private String secretKey;

    @Value("${qr.validity-minutes:5}")
    private int validityMinutes;

    private final SessionRepository sessionRepository;

    /**
     * nonce → timestamp of first use.
     * Prevents a QR token from being used more than once.
     */
    private final ConcurrentHashMap<String, LocalDateTime> usedTokens =
            new ConcurrentHashMap<>();

    // ── Token claims keys ─────────────────────────────────────────────────────

    private static final String CLAIM_SESSION_ID  = "sessionId";
    private static final String CLAIM_LOCATION_ID = "locationId";
    private static final String CLAIM_NONCE       = "nonce";

    // ─────────────────────────────────────────────────────────────────────────
    // GENERATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates a secure QR code for a session.
     *
     * - Reads session start time and location directly from DB
     *   (no manual params needed — just the sessionId)
     * - Token is valid from now until sessionStart + validityMinutes
     * - Token embeds: sessionId, locationId, nonce (UUID), issuedAt
     * - Returns both the raw JWT (for mobile scanning) and
     *   a base64 PNG image (for display on instructor's screen)
     *
     * @param sessionId  the session to generate QR for
     * @return QRResponseDto with rawToken + qrImageBase64
     */
    public QRDto generateForSession(Long sessionId) {

        // ── 1. load session ───────────────────────────────────────────────────
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(
                        "Session not found with id: " + sessionId,
                        HttpStatus.NOT_FOUND));

        if (session.getLocation() == null) {
            throw new CustomException(
                    "Session has no location assigned", HttpStatus.BAD_REQUEST);
        }

        Long   locationId   = session.getLocation().getId();
        String locationName = session.getLocation().getName();

        // ── 2. QR is valid from now until sessionStart + validityMinutes ──────
        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime expiresAt = session.getStartTime().plusMinutes(validityMinutes);

        // Allow generating slightly before session starts too
        if (now.isAfter(session.getEndTime())) {
            throw new CustomException(
                    "Cannot generate QR — session has already ended",
                    HttpStatus.BAD_REQUEST);
        }

        // ── 3. build JWT token ────────────────────────────────────────────────
        String nonce = UUID.randomUUID().toString();

        String rawToken = Jwts.builder()
                .claim(CLAIM_SESSION_ID,  sessionId)
                .claim(CLAIM_LOCATION_ID, locationId)
                .claim(CLAIM_NONCE,       nonce)
                .issuedAt(new Date())
                .expiration(Date.from(
                        expiresAt.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(signingKey())
                .compact();

        // ── 4. encode rawToken string into a QR PNG image ─────────────────────
        String qrImageBase64 = encodeToQRImage(rawToken);

        log.info("✅ QR generated — session: {}, location: {}, expires: {}",
                sessionId, locationId, expiresAt);

        return QRDto.builder()
                .sessionId(sessionId)
                .locationId(locationId)
                .locationName(locationName)
                .expiresAt(expiresAt)
                .rawToken(rawToken)
                .qrImageBase64(qrImageBase64)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // VERIFY
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifies a QR token submitted by a student.
     *
     * Checks (in order):
     *  1. Token signature is valid (not tampered)
     *  2. Token has not expired
     *  3. sessionId claim matches the requested session
     *  4. locationId claim matches the session's actual location in DB
     *  5. Nonce has not been used before (prevents reuse / sharing)
     *
     * @param token     raw JWT scanned from the QR
     * @param sessionId the session the student is trying to attend
     * @return VerificationResult — valid or invalid with reason
     */
    public VerificationResult verifyQR(String token, Long sessionId) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // ── check 2: expiry ───────────────────────────────────────────────
            if (claims.getExpiration().before(new Date())) {
                return VerificationResult.invalid("QR code has expired");
            }

            // ── check 3: sessionId matches ────────────────────────────────────
            Long tokenSessionId = claims.get(CLAIM_SESSION_ID, Long.class);
            if (!tokenSessionId.equals(sessionId)) {
                return VerificationResult.invalid(
                        "QR code is not valid for this session");
            }

            // ── check 4: locationId matches session's actual location ──────────
            Long tokenLocationId = claims.get(CLAIM_LOCATION_ID, Long.class);
            Session session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new CustomException(
                            "Session not found with id: " + sessionId,
                            HttpStatus.NOT_FOUND));

            if (session.getLocation() == null ||
                    !tokenLocationId.equals(session.getLocation().getId())) {
                return VerificationResult.invalid(
                        "QR code location does not match this session's classroom");
            }

            // ── check 5: nonce not reused ─────────────────────────────────────
            String nonce = claims.get(CLAIM_NONCE, String.class);
            if (usedTokens.containsKey(nonce)) {
                return VerificationResult.invalid(
                        "QR code has already been used — each QR can only be scanned once");
            }

            // ── all checks passed → mark nonce as used ────────────────────────
            usedTokens.put(nonce, LocalDateTime.now());
            cleanupOldNonces();

            log.info("✅ QR verified — session: {}, location: {}", sessionId, tokenLocationId);
            return VerificationResult.valid(tokenLocationId);

        } catch (CustomException ce) {
            throw ce; // re-throw domain exceptions
        } catch (Exception e) {
            log.warn("QR verification failed: {}", e.getMessage());
            return VerificationResult.invalid("Invalid QR code: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String encodeToQRImage(String content) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400);
            var image = MatrixToImageWriter.toBufferedImage(matrix);
            var output = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "PNG", output);
            return "data:image/png;base64,"
                    + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (Exception e) {
            log.error("QR image encoding failed", e);
            throw new CustomException("Failed to generate QR image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /** Remove nonces older than 2 hours to avoid unbounded memory growth */
    private void cleanupOldNonces() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(2);
        usedTokens.entrySet().removeIf(e -> e.getValue().isBefore(cutoff));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Result type
    // ─────────────────────────────────────────────────────────────────────────

    @Getter
    @AllArgsConstructor
    public static class VerificationResult {
        private final boolean valid;
        private final String  message;
        /** The locationId from the verified token — used by AttendanceServices */
        private final Long    locationId;

        public static VerificationResult valid(Long locationId) {
            return new VerificationResult(true, null, locationId);
        }

        public static VerificationResult invalid(String message) {
            return new VerificationResult(false, message, null);
        }
    }
}
