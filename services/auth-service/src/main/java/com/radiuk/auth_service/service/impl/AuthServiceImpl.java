package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.annotation.NoLogging;
import com.radiuk.auth_service.dto.*;
import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.*;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.AuthService;
import com.radiuk.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidatorService userValidatorService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByEmail(userRegistrationDto.email())) {
            throw new UserNotCreatedException("User with this email already exists");
        }

        if (userRepository.existsByUsername(userRegistrationDto.username())) {
            throw new UserNotCreatedException("User with this username already exists");
        }

        User user = userMapper.fromRegistrationDto(userRegistrationDto);
        user.setPassword(passwordEncoder.encode(userRegistrationDto.password()));
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent(savedUser, UserAction.CREATED.name());

        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @NoLogging
    @Transactional(readOnly = true)
    public AuthResponse authenticate(UserAuthDto dto) {
        log.debug("Authenticating user {}", dto);

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: user not found");
                    return new BadCredentialsException("Invalid credentials");
                });

        if (passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("Authentication failed: invalid password");
            throw new BadCredentialsException("Invalid credentials");
        }

        log.info("Authenticated user {}", dto);
        return new AuthResponse(jwtService.generateToken(user));
    }
}
