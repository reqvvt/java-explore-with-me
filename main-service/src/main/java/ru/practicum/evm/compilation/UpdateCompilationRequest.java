package ru.practicum.evm.compilation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateCompilationRequest {
    private List<Integer> events;
    private Boolean pinned;
    @NotBlank
    @Size(min = 3, max = 128)
    private String title;
}
