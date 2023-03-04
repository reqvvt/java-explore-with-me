package ru.practicum.ewm.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@RequiredArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Category name should not be blank")
    @Size(min = 2, max = 256)
    private String name;
}
