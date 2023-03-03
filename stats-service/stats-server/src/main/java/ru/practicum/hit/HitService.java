package ru.practicum.hit;

import ru.practicum.statsdto.HitDto;

public interface HitService {
    HitDto save(HitDto hitDto);
}
