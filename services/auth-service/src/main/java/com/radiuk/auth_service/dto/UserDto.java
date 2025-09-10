package com.radiuk.auth_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;

public record UserDto(

        String username,

        String password,

        String firstname,

        String lastname,

        String email,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime registeredAt
) {
}
