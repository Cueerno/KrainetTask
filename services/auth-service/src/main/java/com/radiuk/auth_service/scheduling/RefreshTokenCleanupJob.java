package com.radiuk.auth_service.scheduling;

import com.radiuk.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int deleted = refreshTokenRepository.deleteExpiredOrRevoked(now);

        if (deleted > 0) {
            log.info("RefreshToken cleanup: {} tokens removed", deleted);
        }

        System.out.println("scheduling refresh token cleanup job");
    }
}
