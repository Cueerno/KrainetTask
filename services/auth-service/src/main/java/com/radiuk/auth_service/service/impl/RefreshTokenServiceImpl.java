package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.model.RefreshToken;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.RefreshTokenRepository;
import com.radiuk.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpirationSeconds;

    @Override
    @Transactional
    public String createRefreshToken(User user, String jti) {
        log.debug("Creating refresh token for user {}", user.getUsername());

        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(passwordEncoder.encode(rawToken));
        refreshToken.setJti(jti);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds));

        refreshTokenRepository.save(refreshToken);

        log.info("Created refresh token for user {}", user.getUsername());
        return rawToken;
    }

    @Override
    @Transactional
    public User validateAndGetUser(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findAll().stream()
                .filter(t -> passwordEncoder.matches(refreshToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        log.debug("Validating refresh token for user {}", token.getUser().getUsername());

        if (token.isRevoked()) {
            log.warn("Token has been revoked for user {}", token.getUser().getUsername());
            refreshTokenRepository.revokeAllByUser(token.getUser());
            throw new JwtException("Refresh token reuse detected");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            log.warn("User {} token has expired", token.getUser().getUsername());
            throw new JwtException("Refresh token expired");
        }

        token.setRevoked(true);

        log.info("Refresh token revoked for user {}", token.getUser().getUsername());
        return token.getUser();
    }

    @Override
    @Transactional
    public void revokeByJti(String jti) {
        log.debug("Revoke refresh token for user {}", jti);
        refreshTokenRepository.revokeByJti(jti);
    }

    @Override
    @Transactional
    public void revokeAll(User user) {
        log.debug("Revoke all refresh tokens for user {}", user.getUsername());
        refreshTokenRepository.deleteAllByUser(user);
    }
}
