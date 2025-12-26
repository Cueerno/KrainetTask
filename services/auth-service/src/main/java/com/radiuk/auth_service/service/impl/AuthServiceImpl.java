package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.*;
import com.radiuk.auth_service.event.UserCreatedEvent;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.*;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserValidatorService userValidatorService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationDto userRegistrationDto) {
        log.debug("Registering user with username {} and email {}", userRegistrationDto.username(), userRegistrationDto.email());

        userValidatorService.checkEmailExists(userRegistrationDto.email());
        userValidatorService.checkUsernameExists(userRegistrationDto.username());

        User user = userMapper.fromRegistrationDto(userRegistrationDto);
        user.setPassword(passwordEncoder.encode(userRegistrationDto.password()));
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        applicationEventPublisher.publishEvent(
                new UserCreatedEvent(user.getUsername(), user.getEmail())
        );

        log.info("User {} registered successfully", user.getEmail());

        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse authenticate(UserAuthDto dto) {
        log.debug("Authenticating user with email {}", dto.email());

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: user not found");
                    return new BadCredentialsException("Invalid credentials, user not found");
                });

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            log.warn("Authentication failed: invalid password");
            throw new BadCredentialsException("Invalid credentials: invalid password");
        }

        JwtWithJti jwt = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user, jwt.jti());

        log.info("Authenticated user with email {}", dto.email());
        return new AuthResponse(jwt.accessToken(), refreshToken);
    }

    public void logout(Jwt jwt) {
        log.debug("Logout user {}", jwt.getClaim("email").toString());
        refreshTokenService.revokeByJti(jwt.getId());
        log.info("User {} logged out successfully", jwt.getClaim("email").toString());
    }
}
