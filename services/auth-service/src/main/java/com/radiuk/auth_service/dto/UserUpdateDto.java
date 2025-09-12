package com.radiuk.auth_service.dto;

public record UserUpdateDto(

        String username,

        String firstname,

        String lastname,

        String email
) {
}
