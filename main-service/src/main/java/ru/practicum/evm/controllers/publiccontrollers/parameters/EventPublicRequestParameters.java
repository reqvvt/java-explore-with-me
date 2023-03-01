package ru.practicum.evm.controllers.publiccontrollers.parameters;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class EventPublicRequestParameters {
    private String text;
    private List<Integer> categoryIds;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;

    public void checkTime() {
        if (this.rangeStart == null) {
            this.rangeStart = LocalDateTime.now();
        }
        if (this.rangeEnd == null) {
            this.rangeEnd = LocalDateTime.now().plusYears(5000);
        }
    }
}
