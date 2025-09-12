package com.radiuk.auth_service.mapper;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toUserResponseDto(User user);

    User fromRegistrationDto(UserRegistrationDto userRegistrationDto);
}
