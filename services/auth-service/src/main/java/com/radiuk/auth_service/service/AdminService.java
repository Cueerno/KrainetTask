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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userUpdateDto.email() != null && !userUpdateDto.email().equals(user.getEmail())
                && userRepository.existsByEmail(userUpdateDto.email())) {
            throw new UserNotUpdatedException("User with this email already exists");
        }

        if (userUpdateDto.username() != null && !userUpdateDto.username().equals(user.getUsername())
                && userRepository.existsByUsername(userUpdateDto.username())) {
            throw new UserNotUpdatedException("User with this username already exists");
        }

        userMapper.updateFromDto(userUpdateDto, user);

        User updatedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent(updatedUser, "UPDATED");

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(user.getId());

        userEventPublisher.publishUserEvent(user, "DELETED");
    }
}
