package com.radiuk.auth_service.model;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {
}
