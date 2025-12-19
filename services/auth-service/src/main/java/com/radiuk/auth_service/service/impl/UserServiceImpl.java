package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.model.UserAction;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserManagementService;
import com.radiuk.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(Long.valueOf(jwt.getSubject())));
    }

    @Override
    @Transactional
    public UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        User user = userManagementService.getUserByIdOrThrow(Long.valueOf(jwt.getSubject()));

        userManagementService.updateUser(user, userUpdateDto);

        User updatedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent(updatedUser, UserAction.UPDATED.name());

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteMe(Jwt jwt) {
        User user = userManagementService.getUserByIdOrThrow(Long.valueOf(jwt.getSubject()));

        userRepository.deleteById(user.getId());

        userEventPublisher.publishUserEvent(user, UserAction.DELETED.name());
    }
}
