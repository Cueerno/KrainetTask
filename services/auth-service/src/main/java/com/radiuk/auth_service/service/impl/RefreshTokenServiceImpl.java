package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.model.RefreshToken;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.RefreshTokenRepository;
import com.radiuk.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

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
        String rawToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(passwordEncoder.encode(rawToken));
        refreshToken.setJti(jti);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(refreshTokenExpirationSeconds));

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Override
    @Transactional(readOnly = true)
    public User validateAndGetUser(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findAll().stream()
                .filter(t -> passwordEncoder.matches(refreshToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new JwtException("Invalid refresh token"));

        if (token.isRevoked()) {
            refreshTokenRepository.revokeAllByUser(token.getUser());
            throw new JwtException("Refresh token reuse detected");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new JwtException("Refresh token expired");
        }

        token.setRevoked(true);

        return token.getUser();
    }

    @Override
    @Transactional
    public void revokeByJti(String jti) {
        refreshTokenRepository.revokeByJti(jti);
    }

    @Override
    @Transactional
    public void revokeAll(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }
}
