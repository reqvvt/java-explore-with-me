package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {

    private int id;

    private String email;

    private String name;
}
