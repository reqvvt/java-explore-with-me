package ru.practicum.ewm.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long  id;
    private String created;
    private Long  event;
    private Long  requester;
    private String status;
}
