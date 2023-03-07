package ru.practicum.statsdto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatsDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @PositiveOrZero
    private Long hits;
}
