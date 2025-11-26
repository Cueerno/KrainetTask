package com.radiuk.auth_service.service;

public interface UserValidatorService {

    void validateEmailChange(String newEmail, String currentEmail);

    void validateUsernameChange(String newUsername, String currentUsername);

    void checkEmailExists(String email);

    void checkUsernameExists(String username);
}
