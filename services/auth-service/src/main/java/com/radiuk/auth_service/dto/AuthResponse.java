package com.radiuk.auth_service.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
