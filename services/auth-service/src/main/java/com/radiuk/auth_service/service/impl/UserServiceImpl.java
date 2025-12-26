package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.service.UserManagementService;
import com.radiuk.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserManagementService userManagementService;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        log.debug("Get current user with email {}", jwt.getClaim("email").toString());
        return userMapper.toUserResponseDto(userManagementService.getUserByIdOrThrow(Long.valueOf(jwt.getSubject())));
    }

    @Override
    public UserResponseDto updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        log.debug("Update current user with email {}", jwt.getClaim("email").toString());

        UserResponseDto result = userMapper.toUserResponseDto(userManagementService.updateUserById(userUpdateDto, Long.valueOf(jwt.getSubject())));

        log.debug("Updated current user with email {}", result.email());

        return result;
    }

    @Override
    public void deleteMe(Jwt jwt) {
        log.debug("Delete current user with email {}", jwt.getClaim("email").toString());
        userManagementService.deleteUserById(Long.valueOf(jwt.getSubject()));
        log.debug("Deleted current user with email {}", jwt.getClaim("email").toString());
    }
}
