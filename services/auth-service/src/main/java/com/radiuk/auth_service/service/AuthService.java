package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.AuthResponse;
import com.radiuk.auth_service.dto.UserAuthDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.dto.UserResponseDto;
import org.springframework.security.oauth2.jwt.Jwt;

public interface AuthService {

    UserResponseDto register(UserRegistrationDto userRegistrationDto);

    AuthResponse authenticate(UserAuthDto dto);

    void logout(Jwt jwt);
}
