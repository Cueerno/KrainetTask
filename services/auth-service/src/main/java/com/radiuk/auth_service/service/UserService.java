package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto getMe(Jwt jwt) {
        String email = (String) jwt.getClaims().get("email");

        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
