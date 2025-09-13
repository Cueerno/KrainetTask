package com.radiuk.auth_service.mapper;

import com.radiuk.auth_service.dto.UserResponseDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDto toUserResponseDto(User user);

    User fromRegistrationDto(UserRegistrationDto userRegistrationDto);

    void updateFromDto(UserUpdateDto dto, @MappingTarget User entity);
}
