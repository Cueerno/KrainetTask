package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.exception.UserNotUpdatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.model.UserAction;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        String email = jwt.getSubject();

        return userRepository.findByEmail(email)
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        String email = jwt.getSubject();

        User user = userRepository.findByEmail(email)
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

        userEventPublisher.publishUserEvent(updatedUser, UserAction.UPDATED.name());

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteMe(Jwt jwt) {
        String email = jwt.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(user.getId());

        userEventPublisher.publishUserEvent(user, UserAction.DELETED.name());
    }
}
