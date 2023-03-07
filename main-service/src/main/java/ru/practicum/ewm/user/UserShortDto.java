package ru.practicum.ewm.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;
}
