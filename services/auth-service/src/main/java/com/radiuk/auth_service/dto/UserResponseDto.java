package com.radiuk.auth_service.dto;

import java.time.Instant;

public record UserResponseDto(

        String username,
        String firstname,
        String lastname,
        String email,
        Instant registeredAt
) {
}
