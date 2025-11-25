package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserUpdateDto;

public interface AdminService {

     UserResponseDto getUserById(Long id);

    UserResponseDto updateUserById(UserUpdateDto userUpdateDto, Long id);

    void deleteUserById(Long id);
}
