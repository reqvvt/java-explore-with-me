package ru.practicum.evm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserShortDto {
    private int id;
    private String name;
}
