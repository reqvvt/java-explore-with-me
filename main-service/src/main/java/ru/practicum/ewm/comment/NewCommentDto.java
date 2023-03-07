package ru.practicum.ewm.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank
    private String text;
}
