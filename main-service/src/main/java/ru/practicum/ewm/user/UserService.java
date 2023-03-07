package ru.practicum.ewm.user;


import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto save(NewUserRequest newUserRequest);

    User get(Long userId);

    Collection<UserDto> getAll(List<Long> userIds, @PositiveOrZero Integer from, @Positive Integer size);

    void delete(Long userId);
}
