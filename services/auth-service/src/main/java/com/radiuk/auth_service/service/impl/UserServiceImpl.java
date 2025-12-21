package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.service.UserManagementService;
import com.radiuk.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(Long.valueOf(jwt.getSubject())));
    }

    @Override
    public UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        return userMapper.toUserResponseDto(userManagementService.updateUserById(userUpdateDto, Long.valueOf(jwt.getSubject())));
    }

    @Override
    public void deleteMe(Jwt jwt) {
        userManagementService.deleteUserById(Long.valueOf(jwt.getSubject()));
    }
}
