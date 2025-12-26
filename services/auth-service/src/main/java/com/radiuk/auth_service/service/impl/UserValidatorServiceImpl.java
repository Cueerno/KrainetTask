package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.exception.UserNotUpdatedException;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidatorServiceImpl implements UserValidatorService {

    private static final String USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE = "User with this email already exists";
    private static final String USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE = "User with this username already exists";

    private final UserRepository userRepository;

    @Override
    public void validateEmailChange(String newEmail, String currentEmail) {
        log.debug("Validating email change old {}, new {}", currentEmail, newEmail);
        if (newEmail != null && !newEmail.equals(currentEmail)
                && userRepository.existsByEmail(newEmail)) {
            throw new UserNotUpdatedException(USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void validateUsernameChange(String newUsername, String currentUsername) {
        log.debug("Validating username change old {}, new {}", currentUsername, newUsername);
        if (newUsername != null && !newUsername.equals(currentUsername)
                && userRepository.existsByUsername(newUsername)) {
            throw new UserNotUpdatedException(USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void checkEmailExists(String email) {
        log.debug("Checking email {} uniqueness", email);
        if (userRepository.existsByEmail(email)) {
            throw new UserNotCreatedException(USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void checkUsernameExists(String username) {
        log.debug("Checking username {} uniqueness", username);
        if (userRepository.existsByUsername(username)) {
            throw new UserNotCreatedException(USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE);
        }
    }

}
