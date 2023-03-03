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
    private int id;
    private String created;
    private int event;
    private int requester;
    private String status;
}
