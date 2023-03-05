package ru.practicum.server.hit;

import ru.practicum.statsdto.HitDto;

public interface HitService {
    HitDto save(HitDto hitDto);
}
