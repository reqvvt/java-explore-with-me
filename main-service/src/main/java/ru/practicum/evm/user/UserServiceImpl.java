package ru.practicum.evm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.evm.exception.NotFoundException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User newUser = userMapper.toUser(newUserRequest);
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
    public Collection<UserDto> getUsers(int[] userIds, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        if (userIds.length == 0) {
            return userRepository.findAll(pageRequest).stream()
                                 .map(userMapper::toUserDto)
                                 .collect(Collectors.toList());
        }

        List<Integer> listOfIds = Arrays.stream(userIds)
                                        .boxed()
                                        .collect(Collectors.toList());

        return userRepository.findAllById(listOfIds).stream()
                             .map(userMapper::toUserDto)
                             .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(int userId) {
        userRepository.deleteById(userId);
    }
}
