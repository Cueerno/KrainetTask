package com.radiuk.auth_service.service;

import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.model.User;

public interface UserManagementService {

    User getUserByIdOrThrow(Long id);

    void updateUser(User user, UserUpdateDto userUpdateDto);
}
