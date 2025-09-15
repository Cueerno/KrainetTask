package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserAuthDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.dto.AuthResponse;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long tokenExpirySeconds;

    @Transactional
    public UserResponseDto register(UserRegistrationDto userRegistrationDto) {
        log.debug("Attempting to register user with email={}", userRegistrationDto.email());

        if (userRepository.existsByEmail(userRegistrationDto.email())) {
            log.warn("Registration failed: email={} already exists", userRegistrationDto.email());
            throw new UserNotCreatedException("User with this email already exists");
        }

        if (userRepository.existsByUsername(userRegistrationDto.username())) {
            log.warn("Registration failed: username={} already exists", userRegistrationDto.username());
            throw new UserNotCreatedException("User with this username already exists");
        }

        User user = userMapper.fromRegistrationDto(userRegistrationDto);
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: email={}", savedUser.getEmail());

        userEventPublisher.publishUserEvent(savedUser, "CREATED");

        return userMapper.toUserResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(UserAuthDto dto) {
        log.debug("Authentication attempt for email={}", dto.email());

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: no user with email={}", dto.email());
                    return new BadCredentialsException("Invalid credentials");
                });

        if (!user.getPassword().equals(dto.password())) {
            log.warn("Authentication failed: invalid password for email={}", dto.email());
            throw new BadCredentialsException("Invalid credentials");
        }

        log.info("User authenticated successfully: email={}", user.getEmail());

        return new AuthResponse(generateToken(user), "Bearer", tokenExpirySeconds);
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(tokenExpirySeconds))
                .subject(user.getEmail())
                .claim("authorities", "ROLE_" + user.getRole().name())
                .claim("userId", user.getId())
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims
        );

        String token = jwtEncoder.encode(params).getTokenValue();

        log.debug("JWT generated for email={}", user.getEmail());

        return token;
    }
}
