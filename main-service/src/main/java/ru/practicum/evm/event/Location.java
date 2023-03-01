package ru.practicum.evm.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@RequiredArgsConstructor
@Embeddable
public class Location {
    private float lat;
    private float lon;
}

