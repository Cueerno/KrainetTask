package com.radiuk.auth_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;

public record ErrorResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime timestamp,
        int httpStatus,
        String error,
        Object message
) {}