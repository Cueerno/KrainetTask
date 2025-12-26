package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.event.UserDeletedEvent;
import com.radiuk.auth_service.event.UserUpdatedEvent;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserManagementService;
import com.radiuk.auth_service.service.UserValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserValidatorService userValidatorService;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(Long id) {
        log.debug("Fetching user by id={}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public User updateUserById(UserUpdateDto userUpdateDto, Long id) {
        log.debug("Updating user id={}", id);

        User user = getUserByIdOrThrow(id);

        userValidatorService.validateEmailChange(userUpdateDto.email(), user.getEmail());
        userValidatorService.validateUsernameChange(userUpdateDto.username(), user.getUsername());

        userMapper.updateFromDto(userUpdateDto, user);

        User savedUser = userRepository.save(user);

        applicationEventPublisher.publishEvent(
                new UserUpdatedEvent(savedUser.getUsername(), savedUser.getEmail())
        );

        log.info("User updated id={}", id);

        return savedUser;
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.debug("Deleting user id={}", id);

        User user = getUserByIdOrThrow(id);

        userRepository.deleteById(user.getId());

        applicationEventPublisher.publishEvent(
                new UserDeletedEvent(user.getUsername(), user.getEmail())
        );

        log.info("User deleted id={}", id);
    }
}
