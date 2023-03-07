package ru.practicum.ewm.apiAdmin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(NewUserRequest newUserRequest) {
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
    public Collection<UserDto> getAll(List<Long> userIds, Integer from, Integer size) {
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
    public void delete(Long userId) {
        userRepository.findById(userId)
                      .orElseThrow(() -> new NotFoundException(String.format("User with id=%d was not found", userId)));
        userRepository.deleteById(userId);
    }
}
