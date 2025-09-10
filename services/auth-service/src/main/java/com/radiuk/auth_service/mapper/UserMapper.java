package com.radiuk.auth_service.mapper;

import com.radiuk.auth_service.dto.UserDto;
import com.radiuk.auth_service.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
}
