package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.service.AdminService;
import com.radiuk.auth_service.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        log.debug("Admin get user id={}", id);

        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(id));
    }

    @Override
    public UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id) {
        log.debug("Admin update user id={}", id);

        UserResponseDto result = userMapper.toUserResponseDto(userManagementService.updateUserById(userUpdateDto, id));

        log.info("Admin updated user id={}", id);

        return result;
    }

    @Override
    public void deleteUserById(Long id) {
        log.debug("Admin delete user id={}", id);

        userManagementService.deleteUserById(id);

        log.info("Admin deleted user id={}", id);
    }
}
