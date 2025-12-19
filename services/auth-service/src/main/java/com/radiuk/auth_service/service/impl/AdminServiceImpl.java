package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.model.UserAction;
import com.radiuk.auth_service.publisher.UserEventPublisher;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.AdminService;
import com.radiuk.auth_service.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(id));
    }

    @Override
    @Transactional
    public UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id) {
        User user = userManagementService.getUserByIdOrThrow(id);

        userManagementService.updateUser(user, userUpdateDto);

        User updatedUser = userRepository.save(user);

        userEventPublisher.publishUserEvent(updatedUser, UserAction.UPDATED.name());

        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userManagementService.getUserByIdOrThrow(id);

        userRepository.deleteById(user.getId());

        userEventPublisher.publishUserEvent(user, UserAction.DELETED.name());
    }
}
