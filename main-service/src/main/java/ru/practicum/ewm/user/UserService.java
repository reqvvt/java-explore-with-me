package ru.practicum.ewm.user;


import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    User get(int userId);

    Collection<UserDto> getUsers(List<Integer> userIds, int from, int size);

    void delete(int userId);
}
