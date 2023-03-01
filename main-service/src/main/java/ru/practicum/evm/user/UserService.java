package ru.practicum.evm.user;


import java.util.Collection;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    User get(int userId);

    Collection<UserDto> getUsers(int[] userIds, int from, int size);

    void delete(int userId);
}
