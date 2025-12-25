package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.JwtWithJti;
import com.radiuk.auth_service.model.User;

public interface JwtService {

    JwtWithJti generateToken(User user);
}
