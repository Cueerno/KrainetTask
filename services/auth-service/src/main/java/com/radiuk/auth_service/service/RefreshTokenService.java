package com.radiuk.auth_service.service;

import com.radiuk.auth_service.model.User;

public interface RefreshTokenService {

    String createRefreshToken(User user, String jti);

    User validateAndGetUser(String refreshToken);

    void revokeByJti(String jti);

    void revokeAll(User user);
}
