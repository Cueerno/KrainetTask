package com.radiuk.auth_service.dto;

public record JwtWithJti(

        String accessToken,
        String jti
) {}
