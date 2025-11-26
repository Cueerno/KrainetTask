package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.exception.UserNotCreatedException;
import com.radiuk.auth_service.exception.UserNotUpdatedException;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidatorServiceImpl implements UserValidatorService {

    private static final String USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE = "User with this email already exists";
    private static final String USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE = "User with this username already exists";

    private final UserRepository userRepository;

    @Override
    public void validateEmailChange(String newEmail, String currentEmail) {
        if (newEmail != null && !newEmail.equals(currentEmail)
                && userRepository.existsByEmail(newEmail)) {
            throw new UserNotUpdatedException(USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void validateUsernameChange(String newUsername, String currentUsername) {
        if (newUsername != null && !newUsername.equals(currentUsername)
                && userRepository.existsByUsername(newUsername)) {
            throw new UserNotUpdatedException(USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void checkEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserNotCreatedException(USER_WITH_THIS_EMAIL_ALREADY_EXISTS_MESSAGE);
        }
    }

    @Override
    public void checkUsernameExists(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UserNotCreatedException(USER_WITH_THIS_USERNAME_ALREADY_EXISTS_MESSAGE);
        }
    }

}
