package com.radiuk.auth_service.service;

import com.radiuk.auth_service.model.User;

public interface JwtService {

    String generateToken(User user);
}
