package org.example.smartunipro.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class VerificationResult {
    private boolean valid;
    private String message;

    public static VerificationResult valid() {
        return new VerificationResult(true, null);
    }

    public static VerificationResult invalid(String message) {
        return new VerificationResult(false, message);
    }


}
