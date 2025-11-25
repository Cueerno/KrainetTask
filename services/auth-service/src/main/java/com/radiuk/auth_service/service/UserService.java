package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import org.springframework.security.oauth2.jwt.Jwt;

public interface UserService {

    UserResponseDto getMe(Jwt jwt);

    UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt);

    void deleteMe(Jwt jwt);
}
