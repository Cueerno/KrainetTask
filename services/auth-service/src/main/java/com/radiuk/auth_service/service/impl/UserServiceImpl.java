package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.model.UserAction;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserService;
import com.radiuk.auth_service.service.UserValidatorService;
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
    private final UserValidatorService userValidatorService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        return userRepository.findById(Long.valueOf(jwt.getSubject()))
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        User user = userRepository.findById(Long.valueOf(jwt.getSubject()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userValidatorService.validateEmailChange(userUpdateDto.email(), user.getEmail());
        userValidatorService.validateUsernameChange(userUpdateDto.username(), user.getUsername());

        userMapper.updateFromDto(userUpdateDto, user);

        User updatedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent(updatedUser, UserAction.UPDATED.name());

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteMe(Jwt jwt) {
        User user = userRepository.findById(Long.valueOf(jwt.getSubject()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(user.getId());

        userEventPublisher.publishUserEvent(user, UserAction.DELETED.name());
    }
}
