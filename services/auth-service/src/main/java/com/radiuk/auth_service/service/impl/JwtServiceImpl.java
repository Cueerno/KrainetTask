package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.JwtWithJti;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiration}")
    private long tokenExpirySeconds;

    private final JwtEncoder jwtEncoder;

    public JwtWithJti generateToken(User user) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("auth-service")
                .issuedAt(now)
                .subject(user.getId().toString())
                .expiresAt(now.plusSeconds(tokenExpirySeconds))
                .claim("email", user.getEmail())
                .claim("authorities", List.of(user.getRole().name()))
                .id(jti)
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(), claims
                )
        ).getTokenValue();


        return new JwtWithJti(accessToken, jti);
    }
}
