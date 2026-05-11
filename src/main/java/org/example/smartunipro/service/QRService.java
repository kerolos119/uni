package org.example.smartunipro.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class QRService {

    @Value("${qr.secret:${QR_SECRET:DefaultSecretKey2024!@#}}")
    private String secretKey;

    @Value("${qr.validity-minutes:5}")
    private int validityMinutes;

    private final ConcurrentHashMap<String, QRData> usedTokens = new ConcurrentHashMap<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QRData {
        private String sessionId;
        private String studentId;
        private LocalDateTime timestamp;
    }

    /**
     * توليد QR Code مشفر بأمان عالي
     */
    public String generateSecureQR(Long sessionId, Long locationId, LocalDateTime sessionTime) {
        try {
            String token = Jwts.builder()
                    .claim("sessionId", sessionId)
                    .claim("locationId", locationId)
                    .claim("nonce", UUID.randomUUID().toString())
                    .claim("timestamp", LocalDateTime.now().toString())
                    .issuedAt(new Date())
                    .expiration(Date.from(sessionTime.plusMinutes(validityMinutes)
                            .atZone(ZoneId.systemDefault()).toInstant()))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .compact();

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(token, BarcodeFormat.QR_CODE, 400, 400);
            var image = MatrixToImageWriter.toBufferedImage(matrix);
            var output = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(image, "PNG", output);

            log.info("✅ QR Code generated for session: {}", sessionId);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray());

        } catch (Exception e) {
            log.error("QR generation failed", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * التحقق من صحة QR Code (أمان عالي) - نسخة مبسطة بـ معاملين
     */
    public VerificationResult verifyQR(String token, Long sessionId) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            var claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long tokenSessionId = claims.get("sessionId", Long.class);
            String nonce = claims.get("nonce", String.class);
            Date expiration = claims.getExpiration();

            if (!tokenSessionId.equals(sessionId)) {
                return VerificationResult.invalid("QR code is not for this session");
            }
            if (expiration.before(new Date())) {
                return VerificationResult.invalid("QR code has expired");
            }

            QRData existing = usedTokens.get(nonce);
            if (existing != null) {
                return VerificationResult.invalid("QR code has already been used");
            }

            usedTokens.put(nonce, new QRData(String.valueOf(sessionId), null, LocalDateTime.now()));
            cleanupOldTokens();

            log.info("✅ QR verified for session: {}", sessionId);
            return VerificationResult.valid();

        } catch (Exception e) {
            log.error("QR verification failed", e);
            return VerificationResult.invalid("Invalid QR code: " + e.getMessage());
        }
    }

    private void cleanupOldTokens() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        usedTokens.entrySet().removeIf(entry -> entry.getValue().getTimestamp().isBefore(oneHourAgo));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VerificationResult {
        private boolean valid;
        private String message;

        public static VerificationResult valid() {
            return new VerificationResult(true, null);
        }

        public static VerificationResult invalid(String message) {
            return new VerificationResult(false, message);
        }


    }
}