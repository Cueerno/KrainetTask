package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserAuthDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long tokenExpirySeconds;

    @Transactional
    public void register(UserRegistrationDto userRegistrationDto) {
        Optional<User> existing = userRepository.findByEmail(userRegistrationDto.email());
        if (existing.isPresent()) {
            throw new UserNotCreatedException("User with this email already exists");
        }

        User user = userMapper.fromRegistrationDto(userRegistrationDto);
        user.setPassword(passwordEncoder.encode(userRegistrationDto.password()));
        user.setRole(User.Role.USER);

        userRepository.save(user);
    }

    public String authenticate(UserAuthDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return generateToken(user);
    }

    private String generateToken(User user) {
        OffsetDateTime now = OffsetDateTime.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now.toInstant())
                .expiresAt(now.plusSeconds(tokenExpirySeconds).toInstant())
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtEncoderParameters params = JwtEncoderParameters.from(jwsHeader, claims);

        return jwtEncoder.encode(params).getTokenValue();
    }
}
