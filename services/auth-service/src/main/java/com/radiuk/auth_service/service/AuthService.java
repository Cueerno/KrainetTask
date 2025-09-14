package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserAuthDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.AuthResponse;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long tokenExpirySeconds;

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

        userEventPublisher.publishUserEvent(savedUser, "CREATED");

        return userMapper.toUserResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticate(UserAuthDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

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

        return jwtEncoder.encode(params).getTokenValue();
    }
}
