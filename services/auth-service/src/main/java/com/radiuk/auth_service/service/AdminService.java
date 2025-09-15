package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.exception.UserNotUpdatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.debug("Fetching user profile for id={}", id);

        return userRepository.findById(id)
                .map(user -> {
                    log.info("Successfully fetched profile user with id={}", user.getId());
                    return userMapper.toUserResponseDto(user);
                })
                .orElseThrow(() -> {
                    log.warn("User not found for id={}", id);
                    return new UserNotFoundException("User not found");
                });
    }

    @Transactional
    public UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id) {
        log.debug("Updating user profile for id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot update: user not found for id={}", id);
                    return new UserNotFoundException("User not found");
                });

        if (userUpdateDto.email() != null && !userUpdateDto.email().equals(user.getEmail())
                && userRepository.existsByEmail(userUpdateDto.email())) {
            log.warn("Update failed: email={} already exists", userUpdateDto.email());
            throw new UserNotUpdatedException("User with this email already exists");
        }

        if (userUpdateDto.username() != null && !userUpdateDto.username().equals(user.getUsername())
                && userRepository.existsByUsername(userUpdateDto.username())) {
            log.warn("Update failed: username={} already exists", userUpdateDto.username());
            throw new UserNotUpdatedException("User with this username already exists");
        }

        userMapper.updateFromDto(userUpdateDto, user);

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully: id={}, email={}", updatedUser.getId(), updatedUser.getEmail());

        userEventPublisher.publishUserEvent(updatedUser, "UPDATED");

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.debug("Deleting user with id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete: user not found for id={}", id);
                    return new UserNotFoundException("User not found");
                });

        userRepository.deleteById(user.getId());
        log.info("User deleted successfully: id={}, email={}", user.getId(), user.getEmail());

        userEventPublisher.publishUserEvent(user, "DELETED");
    }
}
