package com.radiuk.auth_service.dto;

public record UserRegistrationDto(

        String username,

        String password,

        String firstname,

        String lastname,

        String email
) {
}
