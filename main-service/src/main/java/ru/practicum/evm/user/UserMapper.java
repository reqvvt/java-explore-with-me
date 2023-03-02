package ru.practicum.evm.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);
}