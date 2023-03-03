package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(NewUserRequest newUserRequest) {
        User newUser;
        if (userRepository.existsUserByName(newUserRequest.getName())) {
            throw new ConflictException(String.format("User name = '%s' is already exists", newUserRequest.getName()));
        } else {
            newUser = userMapper.toUser(newUserRequest);
        }
        return userMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public User get(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                (String.format("User with id = %s was not found", userId))));
    }

    @Override
    @Transactional
    public Collection<UserDto> getUsers(List<Integer> userIds, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        if (userIds.size() == 0) {
            return userRepository.findAll(pageRequest).stream()
                                 .map(userMapper::toUserDto)
                                 .collect(Collectors.toList());
        }

        return userRepository.findAllById(userIds).stream()
                             .map(userMapper::toUserDto)
                             .collect(Collectors.toList());
    }

    @Override
    public void delete(int userId) {
        userRepository.deleteById(userId);
    }
}
