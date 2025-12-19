package com.radiuk.auth_service.service.impl;

import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import com.radiuk.auth_service.service.UserManagementService;
import com.radiuk.auth_service.service.UserValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserValidatorService userValidatorService;
    private final UserMapper userMapper;

    @Override
    public User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void updateUser(User user, UserUpdateDto userUpdateDto) {
        userValidatorService.validateEmailChange(userUpdateDto.email(), user.getEmail());
        userValidatorService.validateUsernameChange(userUpdateDto.username(), user.getUsername());

        userMapper.updateFromDto(userUpdateDto, user);
    }
}
