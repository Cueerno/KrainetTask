package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.exception.UserNotFoundException;
import com.radiuk.auth_service.exception.UserNotUpdatedException;
import com.radiuk.auth_service.mapper.UserMapper;
import com.radiuk.auth_service.model.User;
import com.radiuk.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto getMe(Jwt jwt) {
        return userRepository.findByEmail(jwt.getSubject())
                .map(userMapper::toUserResponseDto)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public void updateMe(UserUpdateDto userUpdateDto, Jwt jwt) {
        if (userRepository.existsByEmail(userUpdateDto.email())) {
            throw new UserNotUpdatedException("User with this email already exists");
        }
        if (userRepository.existsByUsername(userUpdateDto.username())) {
            throw new UserNotUpdatedException("User with this username already exists");
        }

        User user = userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional.ofNullable(userUpdateDto.username()).ifPresent(user::setUsername);
        Optional.ofNullable(userUpdateDto.email()).ifPresent(user::setEmail);
        Optional.ofNullable(userUpdateDto.firstname()).ifPresent(user::setFirstname);
        Optional.ofNullable(userUpdateDto.lastname()).ifPresent(user::setLastname);
    }


    public void deleteMe(Jwt jwt) {
        User user = userRepository.findByEmail(jwt.getSubject())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteById(user.getId());
    }
}
