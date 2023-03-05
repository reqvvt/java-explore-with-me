package ru.practicum.server.hit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.server.mapper.HitMapper;
import ru.practicum.statsdto.HitDto;

@RequiredArgsConstructor
@Service
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto save(HitDto hitDto) {
        return HitMapper.toHitDto(hitRepository.save(HitMapper.toHit(hitDto)));
    }
}
