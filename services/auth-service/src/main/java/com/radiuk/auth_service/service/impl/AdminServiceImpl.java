package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.service.AdminService;
import com.radiuk.auth_service.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(id));
    }

    @Override
    public UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id) {
        return userMapper.toUserResponseDto(userManagementService.updateUserById(userUpdateDto, id));
    }

    @Override
    public void deleteUserById(Long id) {
        userManagementService.deleteUserById(id);
    }
}
