package ru.practicum.ewm.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private String status;
}
