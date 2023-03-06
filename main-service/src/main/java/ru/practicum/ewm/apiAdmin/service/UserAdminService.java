package ru.practicum.ewm.apiAdmin.service;


import ru.practicum.ewm.user.NewUserRequest;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

public interface UserAdminService {

    UserDto save(NewUserRequest newUserRequest);

    Collection<UserDto> getAll(List<Long> userIds, @PositiveOrZero Integer from, @Positive Integer size);

    void delete(Long  userId);
}
