package com.radiuk.auth_service.model;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int httpStatus,
        String error,
        Object message
) {}