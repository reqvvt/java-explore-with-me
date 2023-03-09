package ru.practicum.ewm.comment;

import lombok.*;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.user.UserDto;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private UserDto author;
    private EventShortDto event;
    private String created;
}
