package ru.practicum.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.HitMapper;
import ru.practicum.statsdto.HitDto;

import static ru.practicum.mapper.HitMapper.toHitDto;

@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto save(HitDto hitDto) {
        return toHitDto(hitRepository.save(HitMapper.toHit(hitDto)));
    }
}
