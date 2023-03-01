package ru.practicum.evm.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class EventRequestStatusUpdateRequest {
    private final List<Integer> requestIds;
    private final RequestStatus status;
}
