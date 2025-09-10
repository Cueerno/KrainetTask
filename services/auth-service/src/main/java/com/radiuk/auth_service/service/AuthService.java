package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public void register(UserRegistrationDto userRegistrationDto) {

    }
}
