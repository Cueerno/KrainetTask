package com.radiuk.auth_service.service;

import com.radiuk.auth_service.model.User;

public interface RefreshTokenService {

    String createRefreshToken(User user);

    User validateAndGetUser(String refreshToken);

    void revokeAll(User user);
}
