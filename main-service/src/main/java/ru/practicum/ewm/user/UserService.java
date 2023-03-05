package ru.practicum.ewm.user;


import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest newUserRequest);

    User get(Long  userId);

    Collection<UserDto> getUsers(List<Long> userIds, Long  from, Long  size);

    void delete(Long  userId);
}
